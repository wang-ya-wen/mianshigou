package com.wang.mianshigou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionBatchAddRequest;
import com.wang.mianshigou.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.wang.mianshigou.model.entity.QuestionBankQuestion;
import com.wang.mianshigou.model.entity.User;
import com.wang.mianshigou.model.vo.QuestionBankQuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 校验数据
     *
     * @param QuestionBankQuestion
     * @param add                  对创建的数据进行校验
     */
    void validQuestionBankQuestion(QuestionBankQuestion QuestionBankQuestion, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * 获取题库题目关联封装
     *
     * @param QuestionBankQuestion
     * @param request
     * @return
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion QuestionBankQuestion, HttpServletRequest request);

    /**
     * 分页获取题库题目关联封装
     *
     * @param QuestionBankQuestionPage
     * @param request
     * @return
     */
    Page<QuestionBankQuestionVO> getQuestionBankQuestionVOPage(Page<QuestionBankQuestion> QuestionBankQuestionPage, HttpServletRequest request);

    /**
     * 批量向题库添加题目
     *
     * @param questionIdList
     * @param questionBankId
     * @param loginUser
     */
    void questionBankQuestionAddByBatch(List<Long> questionIdList, long questionBankId, User loginUser);

    /**
     * 批量从题库移除题目
     *
     * @param questionIdList
     * @param questionBankId
     */
    void questionBankQuestionRemoveByBatch(List<Long> questionIdList, long questionBankId);
}