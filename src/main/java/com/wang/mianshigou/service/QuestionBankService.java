package com.wang.mianshigou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.mianshigou.model.entity.QuestionBank;
import com.wang.mianshigou.model.vo.QuestionBankVO;
import com.wang.mianshigou.model.dto.questionBank.QuestionBankQueryRequest;
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
 * @param questionBankQueryRequest
* @return
*/
QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);

/**
* 获取题库封装
*
 * @param questionBank
* @param request
* @return
*/
QuestionBankVO getQuestionBankVO(QuestionBank questionBank, HttpServletRequest request);

/**
* 分页获取题库封装
*
 * @param questionBankPage
* @param request
* @return
*/
Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage, HttpServletRequest request);
    }