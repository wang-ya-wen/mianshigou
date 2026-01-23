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
import com.wang.mianshigou.model.dto.question.QuestionQueryRequest;
import com.wang.mianshigou.model.dto.questionBank.QuestionBankAddRequest;
import com.wang.mianshigou.model.dto.questionBank.QuestionBankEditRequest;
import com.wang.mianshigou.model.dto.questionBank.QuestionBankQueryRequest;
import com.wang.mianshigou.model.dto.questionBank.QuestionBankUpdateRequest;
import com.wang.mianshigou.model.entity.Question;
import com.wang.mianshigou.model.entity.QuestionBank;
import com.wang.mianshigou.model.entity.User;
import com.wang.mianshigou.model.vo.QuestionBankVO;
import com.wang.mianshigou.model.vo.QuestionVO;
import com.wang.mianshigou.service.QuestionBankService;
import com.wang.mianshigou.service.QuestionService;
import com.wang.mianshigou.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* 题库接口
*
*/
@RestController
@RequestMapping("/QuestionBank")
@Slf4j
public class QuestionBankController {

@Resource
private QuestionBankService questionBankService;

@Resource
private UserService userService;
    @Resource

    private QuestionService questionService;

// region 增删改查

/**
* 创建题库
*
* @param QuestionBankAddRequest
* @param request
* @return
*/
@PostMapping("/add")
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest QuestionBankAddRequest, HttpServletRequest request) {
    ThrowUtils.throwIf(QuestionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
    // todo 在此处将实体类和 DTO 进行转换
    QuestionBank QuestionBank = new QuestionBank();
    BeanUtils.copyProperties(QuestionBankAddRequest, QuestionBank);
    // 数据校验
    questionBankService.validQuestionBank(QuestionBank, true);
    // todo 填充默认值
    User loginUser = userService.getLoginUser(request);
    QuestionBank.setUserId(loginUser.getId());
    // 写入数据库
    boolean result = questionBankService.save(QuestionBank);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    // 返回新写入的数据 id
    long newQuestionBankId = QuestionBank.getId();
    return ResultUtils.success(newQuestionBankId);
    }

    /**
    * 删除题库
    *
    * @param deleteRequest
    * @param request
    * @return
    */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)

    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
        }

        /**
        * 更新题库（仅管理员可用）
        *
        * @param QuestionBankUpdateRequest
        * @return
        */
        @PostMapping("/update")
        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
        public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest QuestionBankUpdateRequest) {
            if (QuestionBankUpdateRequest == null || QuestionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            // todo 在此处将实体类和 DTO 进行转换
            QuestionBank QuestionBank = new QuestionBank();
            BeanUtils.copyProperties(QuestionBankUpdateRequest, QuestionBank);
            // 数据校验
            questionBankService.validQuestionBank(QuestionBank, false);
            // 判断是否存在
            long id = QuestionBankUpdateRequest.getId();
            QuestionBank oldQuestionBank = questionBankService.getById(id);
            ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
            // 操作数据库
            boolean result = questionBankService.updateById(QuestionBank);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
            }

            /**
            * 根据 id 获取题库（封装类）
            *
             * @param
            * @return
            */
            @GetMapping("/get/vo")
            public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest request) {
                ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
                // 查询数据库
                Boolean needQuestionList = questionBankQueryRequest.isNeedQuestionList();
                Long id = questionBankQueryRequest.getId();
                QuestionBankVO questionBankVo = new QuestionBankVO();
                QuestionBank questionBank = questionBankService.getById(id);
                ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
                ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
                if (needQuestionList) {
                    QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
                    questionQueryRequest.setQuestionBankId(id);
                    Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
                    Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
                    questionBankVo.setQuestionPage(questionVOPage);
                }


                // 获取封装类
                return ResultUtils.success(questionBankVo);
                }

                /**
                * 分页获取题库列表（仅管理员可用）
                *
                 * @param questionBankQueryRequest
                * @return
                */
                @PostMapping("/list/page")
                public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
                    long current = questionBankQueryRequest.getCurrent();
                    long size = questionBankQueryRequest.getPageSize();
                // 查询数据库
                    Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                            questionBankService.getQueryWrapper(questionBankQueryRequest));
                    return ResultUtils.success(questionBankPage);
                }

                /**
                * 分页获取题库列表（封装类）
                *
                 * @param questionBankQueryRequest
                * @param request
                * @return
                */
                @PostMapping("/list/page/vo")
                public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                    HttpServletRequest request) {
                    long current = questionBankQueryRequest.getCurrent();
                    long size = questionBankQueryRequest.getPageSize();
                    // 限制爬虫
                    ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                    // 查询数据库
                    Page<QuestionBank> QuestionBankPage = questionBankService.page(new Page<>(current, size),
                            questionBankService.getQueryWrapper(questionBankQueryRequest));
                    // 获取封装类
                    return ResultUtils.success(questionBankService.getQuestionBankVOPage(QuestionBankPage, request));
                    }

                    /**
                    * 分页获取当前登录用户创建的题库列表
                    *
                     * @param questionBankQueryRequest
                    * @param request
                    * @return
                    */
                    @PostMapping("/my/list/page/vo")
                    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                        HttpServletRequest request) {
                        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
                        // 补充查询条件，只查询当前登录用户的数据
                        User loginUser = userService.getLoginUser(request);
                        questionBankQueryRequest.setUserId(loginUser.getId());
                        long current = questionBankQueryRequest.getCurrent();
                        long size = questionBankQueryRequest.getPageSize();
                        // 限制爬虫
                        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                        // 查询数据库
                        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                                questionBankService.getQueryWrapper(questionBankQueryRequest));
                        // 获取封装类
                        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage, request));
                        }

                        /**
                        * 编辑题库（给用户使用）
                        *
                        * @param QuestionBankEditRequest
                        * @param request
                        * @return
                        */
                        @PostMapping("/edit")
                        @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)

                        public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest QuestionBankEditRequest, HttpServletRequest request) {
                            if (QuestionBankEditRequest == null || QuestionBankEditRequest.getId() <= 0) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR);
                            }
                            // todo 在此处将实体类和 DTO 进行转换
                            QuestionBank QuestionBank = new QuestionBank();
                            BeanUtils.copyProperties(QuestionBankEditRequest, QuestionBank);
                            // 数据校验
                            questionBankService.validQuestionBank(QuestionBank, false);
                            User loginUser = userService.getLoginUser(request);
                            // 判断是否存在
                            long id = QuestionBankEditRequest.getId();
                            QuestionBank oldQuestionBank = questionBankService.getById(id);
                            ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
                            // 仅本人或管理员可编辑
                            if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                            }
                            // 操作数据库
                            boolean result = questionBankService.updateById(QuestionBank);
                            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                            return ResultUtils.success(true);
                            }

                            // endregion
                            }