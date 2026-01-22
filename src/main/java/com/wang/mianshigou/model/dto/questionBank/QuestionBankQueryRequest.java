package com.wang.mianshigou.model.dto.questionBank;

import com.wang.mianshigou.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
* 查询题库请求

*/
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest implements Serializable {

/**
* id
*/
private Long id;

/**
* id
*/
private Long notId;

/**
* 搜索词
*/
private String searchText;

/**
* 标题
*/
private String title;

/**
* 内容
*/
private String content;
    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;
    /**
     * 是否需要查询题目列表
     */
    private Boolean needQuestionList;

/**
* 标签列表
*/
private List<String> tags;

    /**
    * 创建用户 id
    */
    private Long userId;

    private static final long serialVersionUID = 1L;

    public Boolean isNeedQuestionList() {
        return needQuestionList;
    }

    public void setNeedQuestionList(Boolean needQuestionList) {
        this.needQuestionList = needQuestionList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setNotId(Long notId) {
        this.notId = notId;
    }


    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getNotId() {
        return notId;
    }

    public String getSearchText() {
        return searchText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}