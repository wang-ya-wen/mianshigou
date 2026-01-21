package com.wang.mianshigou.model.vo;

import cn.hutool.json.JSONUtil;
import com.wang.mianshiya.model.entity.QuestionBankQuestion;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 题库题目关联视图
*

*/
@Data
public class QuestionBankQuestionVO implements Serializable {

/**
* id
*/
private Long id;

/**
* 标题
*/
private String title;

/**
* 内容
*/
private String content;

/**
* 创建用户 id
*/
private Long userId;

/**
* 创建时间
*/
private Date createTime;

/**
* 更新时间
*/
private Date updateTime;

/**
* 标签列表
*/
private List<String> tagList;

    /**
    * 创建用户信息
    */
    private UserVO user;

    /**
    * 封装类转对象
    *
    * @param QuestionBankQuestionVO
    * @return
    */
    public static QuestionBankQuestion voToObj(QuestionBankQuestionVO QuestionBankQuestionVO) {
    if (QuestionBankQuestionVO == null) {
    return null;
    }
    QuestionBankQuestion QuestionBankQuestion = new QuestionBankQuestion();
    BeanUtils.copyProperties(QuestionBankQuestionVO, QuestionBankQuestion);
    List<String> tagList = QuestionBankQuestionVO.getTagList();
        QuestionBankQuestion.setTags(JSONUtil.toJsonStr(tagList));
        return QuestionBankQuestion;
        }

        /**
        * 对象转封装类
        *
        * @param QuestionBankQuestion
        * @return
        */
        public static QuestionBankQuestionVO objToVo(QuestionBankQuestion QuestionBankQuestion) {
        if (QuestionBankQuestion == null) {
        return null;
        }
        QuestionBankQuestionVO QuestionBankQuestionVO = new QuestionBankQuestionVO();
        BeanUtils.copyProperties(QuestionBankQuestion, QuestionBankQuestionVO);
        QuestionBankQuestionVO.setTagList(JSONUtil.toList(QuestionBankQuestion.getTags(), String.class));
        return QuestionBankQuestionVO;
        }
        }