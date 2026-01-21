package com.wang.mianshigou.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wang.mianshiya.annotation.AuthCheck;
import com.wang.mianshiya.common.BaseResponse;
import com.wang.mianshiya.common.DeleteRequest;
import com.wang.mianshiya.common.ErrorCode;
import com.wang.mianshiya.common.ResultUtils;
import com.wang.mianshiya.constant.UserConstant;
import com.wang.mianshiya.exception.BusinessException;
import com.wang.mianshiya.exception.ThrowUtils;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankAddRequest;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankEditRequest;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankQueryRequest;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankUpdateRequest;
import com.wang.mianshiya.model.entity.QuestionBank;
import com.wang.mianshiya.model.entity.User;
import com.wang.mianshiya.model.vo.QuestionBankVO;
import com.wang.mianshiya.service.QuestionBankService;
import com.wang.mianshiya.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* 题库接口
*
*/
@RestController
@RequestMapping("/QuestionBank")
@Slf4j
public class QuestionBankController {

@Resource
private QuestionBankService QuestionBankService;

@Resource
private UserService userService;

// region 增删改查

/**
* 创建题库
*
* @param QuestionBankAddRequest
* @param request
* @return
*/
@PostMapping("/add")
public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest QuestionBankAddRequest, HttpServletRequest request) {
    ThrowUtils.throwIf(QuestionBankAddRequest == null, ErrorCode.PARAMS_ERROR);
    // todo 在此处将实体类和 DTO 进行转换
    QuestionBank QuestionBank = new QuestionBank();
    BeanUtils.copyProperties(QuestionBankAddRequest, QuestionBank);
    // 数据校验
    QuestionBankService.validQuestionBank(QuestionBank, true);
    // todo 填充默认值
    User loginUser = userService.getLoginUser(request);
    QuestionBank.setUserId(loginUser.getId());
    // 写入数据库
    boolean result = QuestionBankService.save(QuestionBank);
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
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionBank oldQuestionBank = QuestionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = QuestionBankService.removeById(id);
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
            QuestionBankService.validQuestionBank(QuestionBank, false);
            // 判断是否存在
            long id = QuestionBankUpdateRequest.getId();
            QuestionBank oldQuestionBank = QuestionBankService.getById(id);
            ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
            // 操作数据库
            boolean result = QuestionBankService.updateById(QuestionBank);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            return ResultUtils.success(true);
            }

            /**
            * 根据 id 获取题库（封装类）
            *
            * @param id
            * @return
            */
            @GetMapping("/get/vo")
            public BaseResponse<QuestionBankVO> getQuestionBankVOById(long id, HttpServletRequest request) {
                ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
                // 查询数据库
                QuestionBank QuestionBank = QuestionBankService.getById(id);
                ThrowUtils.throwIf(QuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
                // 获取封装类
                return ResultUtils.success(QuestionBankService.getQuestionBankVO(QuestionBank, request));
                }

                /**
                * 分页获取题库列表（仅管理员可用）
                *
                * @param QuestionBankQueryRequest
                * @return
                */
                @PostMapping("/list/page")
                @SaCheckRole(UserConstant.ADMIN_ROLE)
                public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest QuestionBankQueryRequest) {
                long current = QuestionBankQueryRequest.getCurrent();
                long size = QuestionBankQueryRequest.getPageSize();
                // 查询数据库
                Page<QuestionBank> QuestionBankPage = QuestionBankService.page(new Page<>(current, size),
                QuestionBankService.getQueryWrapper(QuestionBankQueryRequest));
                return ResultUtils.success(QuestionBankPage);
                }

                /**
                * 分页获取题库列表（封装类）
                *
                * @param QuestionBankQueryRequest
                * @param request
                * @return
                */
                @PostMapping("/list/page/vo")
                public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest QuestionBankQueryRequest,
                    HttpServletRequest request) {
                    long current = QuestionBankQueryRequest.getCurrent();
                    long size = QuestionBankQueryRequest.getPageSize();
                    // 限制爬虫
                    ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                    // 查询数据库
                    Page<QuestionBank> QuestionBankPage = QuestionBankService.page(new Page<>(current, size),
                    QuestionBankService.getQueryWrapper(QuestionBankQueryRequest));
                    // 获取封装类
                    return ResultUtils.success(QuestionBankService.getQuestionBankVOPage(QuestionBankPage, request));
                    }

                    /**
                    * 分页获取当前登录用户创建的题库列表
                    *
                    * @param QuestionBankQueryRequest
                    * @param request
                    * @return
                    */
                    @PostMapping("/my/list/page/vo")
                    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest QuestionBankQueryRequest,
                        HttpServletRequest request) {
                        ThrowUtils.throwIf(QuestionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
                        // 补充查询条件，只查询当前登录用户的数据
                        User loginUser = userService.getLoginUser(request);
                        QuestionBankQueryRequest.setUserId(loginUser.getId());
                        long current = QuestionBankQueryRequest.getCurrent();
                        long size = QuestionBankQueryRequest.getPageSize();
                        // 限制爬虫
                        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
                        // 查询数据库
                        Page<QuestionBank> QuestionBankPage = QuestionBankService.page(new Page<>(current, size),
                        QuestionBankService.getQueryWrapper(QuestionBankQueryRequest));
                        // 获取封装类
                        return ResultUtils.success(QuestionBankService.getQuestionBankVOPage(QuestionBankPage, request));
                        }

                        /**
                        * 编辑题库（给用户使用）
                        *
                        * @param QuestionBankEditRequest
                        * @param request
                        * @return
                        */
                        @PostMapping("/edit")
                        public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest QuestionBankEditRequest, HttpServletRequest request) {
                            if (QuestionBankEditRequest == null || QuestionBankEditRequest.getId() <= 0) {
                            throw new BusinessException(ErrorCode.PARAMS_ERROR);
                            }
                            // todo 在此处将实体类和 DTO 进行转换
                            QuestionBank QuestionBank = new QuestionBank();
                            BeanUtils.copyProperties(QuestionBankEditRequest, QuestionBank);
                            // 数据校验
                            QuestionBankService.validQuestionBank(QuestionBank, false);
                            User loginUser = userService.getLoginUser(request);
                            // 判断是否存在
                            long id = QuestionBankEditRequest.getId();
                            QuestionBank oldQuestionBank = QuestionBankService.getById(id);
                            ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
                            // 仅本人或管理员可编辑
                            if (!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
                            }
                            // 操作数据库
                            boolean result = QuestionBankService.updateById(QuestionBank);
                            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                            return ResultUtils.success(true);
                            }

                            // endregion
                            }