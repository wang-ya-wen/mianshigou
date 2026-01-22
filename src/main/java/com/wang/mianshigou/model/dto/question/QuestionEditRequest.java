package com.wang.mianshigou.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* 编辑题目请求

*/
@Data
public class QuestionEditRequest implements Serializable {

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
* 标签列表
*/
private List<String> tags;
    /**
     * 推荐答案
     */
    private String answer;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}