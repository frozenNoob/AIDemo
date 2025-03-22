package com.wb.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data //提供getter和setter函数
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "用户询问AI相关的传递的数据模型")
public class AiQADTO implements Serializable {
    private String justTest;
    @ApiModelProperty("发送过来的消息，包括其角色名和问题")
    private Message[] messages;
    @ApiModelProperty("选择的模型")
    private String model;//Possible values: [deepseek-chat, deepseek-reasoner]
    @ApiModelProperty("惩罚频率，一般为0")
    private int frequency_penalty;
    @ApiModelProperty("最大token数（输入token+输出token）")
    private int max_tokens;

    private int presence_penalty;
    private ResponseFormat response_format;
    @ApiModelProperty("是否选择流运输")
    private boolean stream;
    private double temperature;
    private double top_p;

//    public AiQADTO(String model){
//
//        this.model = model;
//    }
    // Constructor
//    public AiQADTO(Message[] messages, String model, int frequency_penalty, int max_tokens,
//                      int presence_penalty, ResponseFormat response_format, boolean stream,
//                      double temperature, double top_p) {
//        this.messages = messages;
//        this.model = model;
//        this.frequency_penalty = frequency_penalty;
//        this.max_tokens = max_tokens;
//        this.presence_penalty = presence_penalty;
//        this.response_format = response_format;
//        this.stream = stream;
//        this.temperature = temperature;
//        this.top_p = top_p;
//    }

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

    // Inner class for messages
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Message  implements Serializable{
        private String content;
        private String role;

        // Getters or toString(), if needed
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    // Inner class for response format
    public static class ResponseFormat {
        private String type;

        // Getters or toString(), if needed
    }
}
