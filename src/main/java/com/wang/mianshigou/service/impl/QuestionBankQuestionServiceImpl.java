package com.wang.mianshigou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mianshiya.common.ErrorCode;
import com.wang.mianshiya.constant.CommonConstant;
import com.wang.mianshiya.exception.ThrowUtils;
import com.wang.mianshiya.mapper.QuestionBankQuestionMapper;
import com.wang.mianshiya.model.dto.QuestionBankQuestion.QuestionBankQuestionQueryRequest;
import com.wang.mianshiya.model.entity.QuestionBankQuestion;
import com.wang.mianshiya.model.entity.QuestionBankQuestionFavour;
import com.wang.mianshiya.model.entity.QuestionBankQuestionThumb;
import com.wang.mianshiya.model.entity.User;
import com.wang.mianshiya.model.vo.QuestionBankQuestionVO;
import com.wang.mianshiya.model.vo.UserVO;
import com.wang.mianshiya.service.QuestionBankQuestionService;
import com.wang.mianshiya.service.UserService;
import com.wang.mianshiya.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* 题库题目关联服务实现
*

*/
@Service
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

@Resource
private UserService userService;

/**
* 校验数据
*
* @param QuestionBankQuestion
* @param add      对创建的数据进行校验
*/
@Override
public void validQuestionBankQuestion(QuestionBankQuestion QuestionBankQuestion, boolean add) {
ThrowUtils.throwIf(QuestionBankQuestion == null, ErrorCode.PARAMS_ERROR);
// todo 从对象中取值
String title = QuestionBankQuestion.getTitle();
// 创建数据时，参数不能为空
if (add) {
// todo 补充校验规则
ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
}
// 修改数据时，有参数则校验
// todo 补充校验规则
if (StringUtils.isNotBlank(title)) {
ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
}
}

/**
* 获取查询条件
*
* @param QuestionBankQuestionQueryRequest
* @return
*/
@Override
public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest QuestionBankQuestionQueryRequest) {
QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
if (QuestionBankQuestionQueryRequest == null) {
return queryWrapper;
}
// todo 从对象中取值
Long id = QuestionBankQuestionQueryRequest.getId();
Long notId = QuestionBankQuestionQueryRequest.getNotId();
String title = QuestionBankQuestionQueryRequest.getTitle();
String content = QuestionBankQuestionQueryRequest.getContent();
String searchText = QuestionBankQuestionQueryRequest.getSearchText();
String sortField = QuestionBankQuestionQueryRequest.getSortField();
String sortOrder = QuestionBankQuestionQueryRequest.getSortOrder();
List<String> tagList = QuestionBankQuestionQueryRequest.getTags();
    Long userId = QuestionBankQuestionQueryRequest.getUserId();
    // todo 补充需要的查询条件
    // 从多字段中搜索
    if (StringUtils.isNotBlank(searchText)) {
    // 需要拼接查询条件
    queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
    }
    // 模糊查询
    queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
    queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
    // JSON 数组查询
    if (CollUtil.isNotEmpty(tagList)) {
    for (String tag : tagList) {
    queryWrapper.like("tags", "\"" + tag + "\"");
    }
    }
    // 精确查询
    queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
    queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
    queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
    // 排序规则
    queryWrapper.orderBy(SqlUtils.validSortField(sortField),
    sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
    sortField);
    return queryWrapper;
    }

    /**
    * 获取题库题目关联封装
    *
    * @param QuestionBankQuestion
    * @param request
    * @return
    */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion QuestionBankQuestion, HttpServletRequest request) {
    // 对象转封装类
    QuestionBankQuestionVO QuestionBankQuestionVO = QuestionBankQuestionVO.objToVo(QuestionBankQuestion);

    // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
    // region 可选
    // 1. 关联查询用户信息
    Long userId = QuestionBankQuestion.getUserId();
    User user = null;
    if (userId != null && userId > 0) {
    user = userService.getById(userId);
    }
    UserVO userVO = userService.getUserVO(user);
    QuestionBankQuestionVO.setUser(userVO);
    // 2. 已登录，获取用户点赞、收藏状态
    long QuestionBankQuestionId = QuestionBankQuestion.getId();
    User loginUser = userService.getLoginUserPermitNull(request);
    if (loginUser != null) {
    // 获取点赞
    QueryWrapper<QuestionBankQuestionThumb> QuestionBankQuestionThumbQueryWrapper = new QueryWrapper<>();
        QuestionBankQuestionThumbQueryWrapper.in("QuestionBankQuestionId", QuestionBankQuestionId);
        QuestionBankQuestionThumbQueryWrapper.eq("userId", loginUser.getId());
        QuestionBankQuestionThumb QuestionBankQuestionThumb = QuestionBankQuestionThumbMapper.selectOne(QuestionBankQuestionThumbQueryWrapper);
        QuestionBankQuestionVO.setHasThumb(QuestionBankQuestionThumb != null);
        // 获取收藏
        QueryWrapper<QuestionBankQuestionFavour> QuestionBankQuestionFavourQueryWrapper = new QueryWrapper<>();
            QuestionBankQuestionFavourQueryWrapper.in("QuestionBankQuestionId", QuestionBankQuestionId);
            QuestionBankQuestionFavourQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankQuestionFavour QuestionBankQuestionFavour = QuestionBankQuestionFavourMapper.selectOne(QuestionBankQuestionFavourQueryWrapper);
            QuestionBankQuestionVO.setHasFavour(QuestionBankQuestionFavour != null);
            }
            // endregion

            return QuestionBankQuestionVO;
            }

            /**
            * 分页获取题库题目关联封装
            *
            * @param QuestionBankQuestionPage
            * @param request
            * @return
            */
            @Override
            public Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> QuestionBankQuestionPage, HttpServletRequest request) {
                List<QuestionBankQuestion> QuestionBankQuestionList = QuestionBankQuestionPage.getRecords();
                Page<QuestionBankQuestionVO> QuestionBankQuestionVOPage = new Page<>(QuestionBankQuestionPage.getCurrent(), QuestionBankQuestionPage.getSize(), QuestionBankQuestionPage.getTotal());
                    if (CollUtil.isEmpty(QuestionBankQuestionList)) {
                    return QuestionBankQuestionVOPage;
                    }
                    // 对象列表 => 封装对象列表
                    List<QuestionBankQuestionVO> QuestionBankQuestionVOList = QuestionBankQuestionList.stream().map(QuestionBankQuestion -> {
                        return QuestionBankQuestionVO.objToVo(QuestionBankQuestion);
                        }).collect(Collectors.toList());

                        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
                        // region 可选
                        // 1. 关联查询用户信息
                        Set<Long> userIdSet = QuestionBankQuestionList.stream().map(QuestionBankQuestion::getUserId).collect(Collectors.toSet());
                            Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                                .collect(Collectors.groupingBy(User::getId));
                                // 2. 已登录，获取用户点赞、收藏状态
                                Map<Long, Boolean> QuestionBankQuestionIdHasThumbMap = new HashMap<>();
                                Map<Long, Boolean> QuestionBankQuestionIdHasFavourMap = new HashMap<>();
                                User loginUser = userService.getLoginUserPermitNull(request);
                                if (loginUser != null) {
                                Set<Long> QuestionBankQuestionIdSet = QuestionBankQuestionList.stream().map(QuestionBankQuestion::getId).collect(Collectors.toSet());
                                    loginUser = userService.getLoginUser(request);
                                    // 获取点赞
                                    QueryWrapper<QuestionBankQuestionThumb> QuestionBankQuestionThumbQueryWrapper = new QueryWrapper<>();
                                        QuestionBankQuestionThumbQueryWrapper.in("QuestionBankQuestionId", QuestionBankQuestionIdSet);
                                        QuestionBankQuestionThumbQueryWrapper.eq("userId", loginUser.getId());
                                        List<QuestionBankQuestionThumb> QuestionBankQuestionQuestionBankQuestionThumbList = QuestionBankQuestionThumbMapper.selectList(QuestionBankQuestionThumbQueryWrapper);
                                            QuestionBankQuestionQuestionBankQuestionThumbList.forEach(QuestionBankQuestionQuestionBankQuestionThumb -> QuestionBankQuestionIdHasThumbMap.put(QuestionBankQuestionQuestionBankQuestionThumb.getQuestionBankQuestionId(), true));
                                            // 获取收藏
                                            QueryWrapper<QuestionBankQuestionFavour> QuestionBankQuestionFavourQueryWrapper = new QueryWrapper<>();
                                                QuestionBankQuestionFavourQueryWrapper.in("QuestionBankQuestionId", QuestionBankQuestionIdSet);
                                                QuestionBankQuestionFavourQueryWrapper.eq("userId", loginUser.getId());
                                                List<QuestionBankQuestionFavour> QuestionBankQuestionFavourList = QuestionBankQuestionFavourMapper.selectList(QuestionBankQuestionFavourQueryWrapper);
                                                    QuestionBankQuestionFavourList.forEach(QuestionBankQuestionFavour -> QuestionBankQuestionIdHasFavourMap.put(QuestionBankQuestionFavour.getQuestionBankQuestionId(), true));
                                                    }
                                                    // 填充信息
                                                    QuestionBankQuestionVOList.forEach(QuestionBankQuestionVO -> {
                                                    Long userId = QuestionBankQuestionVO.getUserId();
                                                    User user = null;
                                                    if (userIdUserListMap.containsKey(userId)) {
                                                    user = userIdUserListMap.get(userId).get(0);
                                                    }
                                                    QuestionBankQuestionVO.setUser(userService.getUserVO(user));
                                                    QuestionBankQuestionVO.setHasThumb(QuestionBankQuestionIdHasThumbMap.getOrDefault(QuestionBankQuestionVO.getId(), false));
                                                    QuestionBankQuestionVO.setHasFavour(QuestionBankQuestionIdHasFavourMap.getOrDefault(QuestionBankQuestionVO.getId(), false));
                                                    });
                                                    // endregion

                                                    QuestionBankQuestionVOPage.setRecords(QuestionBankQuestionVOList);
                                                    return QuestionBankQuestionVOPage;
                                                    }

                                                    }