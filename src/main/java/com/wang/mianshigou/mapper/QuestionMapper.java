package com.wang.mianshigou.mapper;

import com.wang.mianshigou.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @Entity generator.domain.Question
 */
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 查询题目列表（包括已删除的数据)
     * @param minUpdateTime
     * @return
     */
    @Select("select * from question where updateTime > #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);
}




