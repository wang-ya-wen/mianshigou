package com.wang.mianshigou.model.dto.questionBank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* 创建题库请求

*/
@Data
public class QuestionBankAddRequest implements Serializable {

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
* 标签列表
*/
private List<String> tags;

    private static final long serialVersionUID = 1L;

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