package com.wang.mianshigou.model.vo;

import cn.hutool.json.JSONUtil;
import com.wang.mianshiya.model.entity.QuestionBank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 题库视图
*

*/
@Data
public class QuestionBankVO implements Serializable {

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
    * @param QuestionBankVO
    * @return
    */
    public static QuestionBank voToObj(QuestionBankVO QuestionBankVO) {
    if (QuestionBankVO == null) {
    return null;
    }
    QuestionBank QuestionBank = new QuestionBank();
    BeanUtils.copyProperties(QuestionBankVO, QuestionBank);
    List<String> tagList = QuestionBankVO.getTagList();
        QuestionBank.setTags(JSONUtil.toJsonStr(tagList));
        return QuestionBank;
        }

        /**
        * 对象转封装类
        *
        * @param QuestionBank
        * @return
        */
        public static QuestionBankVO objToVo(QuestionBank QuestionBank) {
        if (QuestionBank == null) {
        return null;
        }
        QuestionBankVO QuestionBankVO = new QuestionBankVO();
        BeanUtils.copyProperties(QuestionBank, QuestionBankVO);
        QuestionBankVO.setTagList(JSONUtil.toList(QuestionBank.getTags(), String.class));
        return QuestionBankVO;
        }
        }