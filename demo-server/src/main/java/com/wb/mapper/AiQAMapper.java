package com.wb.mapper;

import com.wb.entity.QAItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiQAMapper {
    @Insert("insert into qa(user_id, question, answer,prompt_tokens, completion_tokens)" +
            " VALUES (#{userId},#{question},#{answer}," +
            "#{prompt_tokens},#{completion_tokens})")
    void insert(QAItem qaItem);
}