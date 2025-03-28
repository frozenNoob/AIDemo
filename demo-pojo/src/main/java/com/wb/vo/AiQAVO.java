package com.wb.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * AI问答
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AiQAVO {
    //回复
    private String answer;
    //输入token
    private int prompt_tokens;
    //输出token
    private int completion_tokens;

    public String toJson() {
        // Convert this object to JSON string using your preferred library
        // You can use Jackson, Gson etc.
        // Example using Jackson:
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
