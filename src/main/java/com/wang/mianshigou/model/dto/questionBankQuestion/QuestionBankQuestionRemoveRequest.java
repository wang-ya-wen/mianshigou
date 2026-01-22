package com.wang.mianshigou.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;

/**
 * 移除题库题目关联表
 */
@Data
public class QuestionBankQuestionRemoveRequest implements Serializable {
    /**
     * 题库id
     */
    private Long questionBankId;
    /**
     * 题目id
     */
    private Long questionId;
    private static final long serialVersionUID = 1L;

    public Long getQuestionBankId() {
        return questionBankId;
    }

    public void setQuestionBankId(Long questionBankId) {
        this.questionBankId = questionBankId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
