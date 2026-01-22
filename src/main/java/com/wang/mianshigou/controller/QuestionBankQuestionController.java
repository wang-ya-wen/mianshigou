package com.wang.mianshigou.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.mianshigou.annotation.AuthCheck;
import com.wang.mianshigou.common.BaseResponse;
import com.wang.mianshigou.common.DeleteRequest;
import com.wang.mianshigou.common.ErrorCode;
import com.wang.mianshigou.common.ResultUtils;
import com.wang.mianshigou.constant.UserConstant;
import com.wang.mianshigou.exception.BusinessException;
import com.wang.mianshigou.exception.ThrowUtils;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionUpdateRequest;
import com.wang.mianshigou.model.entity.QuestionBankQuestion;
import com.wang.mianshigou.model.entity.User;
import com.wang.mianshigou.model.vo.QuestionBankQuestionVO;
import com.wang.mianshigou.service.QuestionBankQuestionService;
import com.wang.mianshigou.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* 题库题目关联接口
*
*/
@RestController
@RequestMapping("/QuestionBankQuestion")
@Slf4j
public class QuestionBankQuestionController {

@Resource
private QuestionBankQuestionService questionBankQuestionService;

@Resource
private UserService userService;

// region 增删改查

/**
* 创建题库题目关联
*
* @param QuestionBankQuestionAddRequest
* @param request
* @return
*/
@PostMapping("/add")
public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest QuestionBankQuestionAddRequest, HttpServletRequest request) {
    ThrowUtils.throwIf(QuestionBankQuestionAddRequest == null, ErrorCode.PARAMS_ERROR);
    // todo 在此处将实体类和 DTO 进行转换
    QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
    BeanUtils.copyProperties(QuestionBankQuestionAddRequest, questionBankQuestion);
    // 数据校验
    questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, true);
    // todo 填充默认值
    User loginUser = userService.getLoginUser(request);
    questionBankQuestion.setUserId(loginUser.getId());
    // 写入数据库
    boolean result = questionBankQuestionService.save(questionBankQuestion);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    // 返回新写入的数据 id
    long newQuestionBankQuestionId = questionBankQuestion.getId();
    return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
    * 删除题库题目关联
    *
    * @param deleteRequest
    * @param request
    * @return
    */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBankQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankQuestionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
        }

        /**
        * 更新题库题目关联（仅管理员可用）
        *
         * @param questionBankQuestionUpdateRequest
        * @return
        */
        @PostMapping("/update")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest) {
            if (questionBankQuestionUpdateRequest == null || questionBankQuestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // todo 在此处将实体类和 DTO 进行转换
            QuestionBankQuestion QuestionBankQuestion = new QuestionBankQuestion();
            BeanUtils.copyProperties(questionBankQuestionUpdateRequest, QuestionBankQuestion);
            // 数据校验
            questionBankQuestionService.validQuestionBankQuestion(QuestionBankQuestion, false);
            // 判断是否存在
            long id = questionBankQuestionUpdateRequest.getId();
            QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
            ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
            // 操作数据库
            boolean result = questionBankQuestionService.updateById(QuestionBankQuestion);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
            }

            /**
            * 根据 id 获取题库题目关联（封装类）
            *
            * @param id
            * @return
            */
            @GetMapping("/get/vo")
            public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
                ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
                // 查询数据库
                QuestionBankQuestion QuestionBankQuestion = questionBankQuestionService.getById(id);
                ThrowUtils.throwIf(QuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
                // 获取封装类
                return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVO(QuestionBankQuestion, request));
                }

                /**
                * 分页获取题库题目关联列表（仅管理员可用）
                *
                 * @param questionBankQuestionQueryRequest
                * @return
                */
                @PostMapping("/list/page")
                @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
                public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
                    long current = questionBankQuestionQueryRequest.getCurrent();
                    long size = questionBankQuestionQueryRequest.getPageSize();
                // 查询数据库
                    Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
                            questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
                    return ResultUtils.success(questionBankQuestionPage);
                }

                /**
                * 分页获取题库题目关联列表（封装类）
                *
                 * @param questionBankQuestionQueryRequest
                * @param request
                * @return
                */
                @PostMapping("/list/page/vo")
                public BaseResponse<Page<QuestionBankQuestionVO>> listQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
                    HttpServletRequest request) {
                    long current = questionBankQuestionQueryRequest.getCurrent();
                    long size = questionBankQuestionQueryRequest.getPageSize();
                    // 限制爬虫
                    ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                    // 查询数据库
                    Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
                            questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
                    // 获取封装类
                    return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage, request));
                    }

                    /**
                    * 分页获取当前登录用户创建的题库题目关联列表
                    *
                     * @param questionBankQuestionQueryRequest
                    * @param request
                    * @return
                    */
                    @PostMapping("/my/list/page/vo")
                    public BaseResponse<Page<QuestionBankQuestionVO>> listMyQuestionBankQuestionVOByPage(@RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest,
                        HttpServletRequest request) {
                        ThrowUtils.throwIf(questionBankQuestionQueryRequest == null, ErrorCode.PARAMS_ERROR);
                        // 补充查询条件，只查询当前登录用户的数据
                        User loginUser = userService.getLoginUser(request);
                        questionBankQuestionQueryRequest.setUserId(loginUser.getId());
                        long current = questionBankQuestionQueryRequest.getCurrent();
                        long size = questionBankQuestionQueryRequest.getPageSize();
                        // 限制爬虫
                        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                        // 查询数据库
                        Page<QuestionBankQuestion> questionBankQuestionPage = questionBankQuestionService.page(new Page<>(current, size),
                                questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
                        // 获取封装类
                        return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVOPage(questionBankQuestionPage, request));
                        }


    // endregion
                            }