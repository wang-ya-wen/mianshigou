package com.wang.mianshigou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.mianshiya.model.dto.QuestionBank.QuestionBankQueryRequest;
import com.wang.mianshiya.model.entity.QuestionBank;
import com.wang.mianshiya.model.vo.QuestionBankVO;

import javax.servlet.http.HttpServletRequest;

/**
* 题库服务
*
*/
public interface QuestionBankService extends IService<QuestionBank> {

/**
* 校验数据
*
* @param QuestionBank
* @param add 对创建的数据进行校验
*/
void validQuestionBank(QuestionBank QuestionBank, boolean add);

/**
* 获取查询条件
*
* @param QuestionBankQueryRequest
* @return
*/
QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest QuestionBankQueryRequest);

/**
* 获取题库封装
*
* @param QuestionBank
* @param request
* @return
*/
QuestionBankVO getQuestionBankVO(QuestionBank QuestionBank, HttpServletRequest request);

/**
* 分页获取题库封装
*
* @param QuestionBankPage
* @param request
* @return
*/
Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> QuestionBankPage, HttpServletRequest request);
    }