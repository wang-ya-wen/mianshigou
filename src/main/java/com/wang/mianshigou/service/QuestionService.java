package com.wang.mianshigou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.mianshigou.model.dto.post.PostQueryRequest;
import com.wang.mianshigou.model.dto.question.QuestionQueryRequest;
import com.wang.mianshigou.model.entity.Post;
import com.wang.mianshigou.model.entity.Question;
import com.wang.mianshigou.model.vo.QuestionVO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
* 题目服务
*
*/
public interface QuestionService extends IService<Question> {

/**
* 校验数据
*
* @param Question
* @param add 对创建的数据进行校验
*/
void validQuestion(Question Question, boolean add);

/**
* 获取查询条件
*
 * @param questionQueryRequest
* @return
*/
QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

/**
* 获取题目封装
*
* @param Question
* @param request
* @return
*/
QuestionVO getQuestionVO(Question Question, HttpServletRequest request);

/**
* 分页获取题目封装
*
* @param QuestionPage
* @param request
* @return
*/
Page<QuestionVO> getQuestionVOPage(Page<Question> QuestionPage, HttpServletRequest request);

    /**
     * 根据题库id查询题目列表
     *
     * @param questionQueryRequest
     * @return
     */
    Page<Question> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest);

    /**
     * 从 ES 查询
     *
     * @param questionQueryRequest
     * @return
     */
    Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest);
}