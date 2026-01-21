package com.wang.mianshigou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.mianshiya.common.ErrorCode;
import com.wang.mianshiya.constant.CommonConstant;
import com.wang.mianshiya.exception.ThrowUtils;
import com.wang.mianshiya.mapper.QuestionBankMapper;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankQueryRequest;
import com.wang.mianshiya.model.entity.QuestionBank;
import com.wang.mianshiya.model.entity.QuestionBankFavour;
import com.wang.mianshiya.model.entity.QuestionBankThumb;
import com.wang.mianshiya.model.entity.User;
import com.wang.mianshiya.model.vo.QuestionBankVO;
import com.wang.mianshiya.model.vo.UserVO;
import com.wang.mianshiya.service.QuestionBankService;
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
* 题库服务实现
*

*/
@Service
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

@Resource
private UserService userService;

/**
* 校验数据
*
* @param QuestionBank
* @param add      对创建的数据进行校验
*/
@Override
public void validQuestionBank(QuestionBank QuestionBank, boolean add) {
ThrowUtils.throwIf(QuestionBank == null, ErrorCode.PARAMS_ERROR);
// todo 从对象中取值
String title = QuestionBank.getTitle();
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
* @param QuestionBankQueryRequest
* @return
*/
@Override
public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest QuestionBankQueryRequest) {
QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
if (QuestionBankQueryRequest == null) {
return queryWrapper;
}
// todo 从对象中取值
Long id = QuestionBankQueryRequest.getId();
Long notId = QuestionBankQueryRequest.getNotId();
String title = QuestionBankQueryRequest.getTitle();
String content = QuestionBankQueryRequest.getContent();
String searchText = QuestionBankQueryRequest.getSearchText();
String sortField = QuestionBankQueryRequest.getSortField();
String sortOrder = QuestionBankQueryRequest.getSortOrder();
List<String> tagList = QuestionBankQueryRequest.getTags();
    Long userId = QuestionBankQueryRequest.getUserId();
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
    * 获取题库封装
    *
    * @param QuestionBank
    * @param request
    * @return
    */
    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank QuestionBank, HttpServletRequest request) {
    // 对象转封装类
    QuestionBankVO QuestionBankVO = QuestionBankVO.objToVo(QuestionBank);

    // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
    // region 可选
    // 1. 关联查询用户信息
    Long userId = QuestionBank.getUserId();
    User user = null;
    if (userId != null && userId > 0) {
    user = userService.getById(userId);
    }
    UserVO userVO = userService.getUserVO(user);
    QuestionBankVO.setUser(userVO);
    // 2. 已登录，获取用户点赞、收藏状态
    long QuestionBankId = QuestionBank.getId();
    User loginUser = userService.getLoginUserPermitNull(request);
    if (loginUser != null) {
    // 获取点赞
    QueryWrapper<QuestionBankThumb> QuestionBankThumbQueryWrapper = new QueryWrapper<>();
        QuestionBankThumbQueryWrapper.in("QuestionBankId", QuestionBankId);
        QuestionBankThumbQueryWrapper.eq("userId", loginUser.getId());
        QuestionBankThumb QuestionBankThumb = QuestionBankThumbMapper.selectOne(QuestionBankThumbQueryWrapper);
        QuestionBankVO.setHasThumb(QuestionBankThumb != null);
        // 获取收藏
        QueryWrapper<QuestionBankFavour> QuestionBankFavourQueryWrapper = new QueryWrapper<>();
            QuestionBankFavourQueryWrapper.in("QuestionBankId", QuestionBankId);
            QuestionBankFavourQueryWrapper.eq("userId", loginUser.getId());
            QuestionBankFavour QuestionBankFavour = QuestionBankFavourMapper.selectOne(QuestionBankFavourQueryWrapper);
            QuestionBankVO.setHasFavour(QuestionBankFavour != null);
            }
            // endregion

            return QuestionBankVO;
            }

            /**
            * 分页获取题库封装
            *
            * @param QuestionBankPage
            * @param request
            * @return
            */
            @Override
            public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> QuestionBankPage, HttpServletRequest request) {
                List<QuestionBank> QuestionBankList = QuestionBankPage.getRecords();
                Page<QuestionBankVO> QuestionBankVOPage = new Page<>(QuestionBankPage.getCurrent(), QuestionBankPage.getSize(), QuestionBankPage.getTotal());
                    if (CollUtil.isEmpty(QuestionBankList)) {
                    return QuestionBankVOPage;
                    }
                    // 对象列表 => 封装对象列表
                    List<QuestionBankVO> QuestionBankVOList = QuestionBankList.stream().map(QuestionBank -> {
                        return QuestionBankVO.objToVo(QuestionBank);
                        }).collect(Collectors.toList());

                        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
                        // region 可选
                        // 1. 关联查询用户信息
                        Set<Long> userIdSet = QuestionBankList.stream().map(QuestionBank::getUserId).collect(Collectors.toSet());
                            Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                                .collect(Collectors.groupingBy(User::getId));
                                // 2. 已登录，获取用户点赞、收藏状态
                                Map<Long, Boolean> QuestionBankIdHasThumbMap = new HashMap<>();
                                Map<Long, Boolean> QuestionBankIdHasFavourMap = new HashMap<>();
                                User loginUser = userService.getLoginUserPermitNull(request);
                                if (loginUser != null) {
                                Set<Long> QuestionBankIdSet = QuestionBankList.stream().map(QuestionBank::getId).collect(Collectors.toSet());
                                    loginUser = userService.getLoginUser(request);
                                    // 获取点赞
                                    QueryWrapper<QuestionBankThumb> QuestionBankThumbQueryWrapper = new QueryWrapper<>();
                                        QuestionBankThumbQueryWrapper.in("QuestionBankId", QuestionBankIdSet);
                                        QuestionBankThumbQueryWrapper.eq("userId", loginUser.getId());
                                        List<QuestionBankThumb> QuestionBankQuestionBankThumbList = QuestionBankThumbMapper.selectList(QuestionBankThumbQueryWrapper);
                                            QuestionBankQuestionBankThumbList.forEach(QuestionBankQuestionBankThumb -> QuestionBankIdHasThumbMap.put(QuestionBankQuestionBankThumb.getQuestionBankId(), true));
                                            // 获取收藏
                                            QueryWrapper<QuestionBankFavour> QuestionBankFavourQueryWrapper = new QueryWrapper<>();
                                                QuestionBankFavourQueryWrapper.in("QuestionBankId", QuestionBankIdSet);
                                                QuestionBankFavourQueryWrapper.eq("userId", loginUser.getId());
                                                List<QuestionBankFavour> QuestionBankFavourList = QuestionBankFavourMapper.selectList(QuestionBankFavourQueryWrapper);
                                                    QuestionBankFavourList.forEach(QuestionBankFavour -> QuestionBankIdHasFavourMap.put(QuestionBankFavour.getQuestionBankId(), true));
                                                    }
                                                    // 填充信息
                                                    QuestionBankVOList.forEach(QuestionBankVO -> {
                                                    Long userId = QuestionBankVO.getUserId();
                                                    User user = null;
                                                    if (userIdUserListMap.containsKey(userId)) {
                                                    user = userIdUserListMap.get(userId).get(0);
                                                    }
                                                    QuestionBankVO.setUser(userService.getUserVO(user));
                                                    QuestionBankVO.setHasThumb(QuestionBankIdHasThumbMap.getOrDefault(QuestionBankVO.getId(), false));
                                                    QuestionBankVO.setHasFavour(QuestionBankIdHasFavourMap.getOrDefault(QuestionBankVO.getId(), false));
                                                    });
                                                    // endregion

                                                    QuestionBankVOPage.setRecords(QuestionBankVOList);
                                                    return QuestionBankVOPage;
                                                    }

                                                    }