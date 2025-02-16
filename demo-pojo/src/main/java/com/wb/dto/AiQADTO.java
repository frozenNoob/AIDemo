package com.wb.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data //提供getter和setter函数
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiQADTO implements Serializable {
    private long userId;
    private String justTest;
    private Message[] messages;
    private String model;//Possible values: [deepseek-chat, deepseek-reasoner]
    private int frequency_penalty;
    private int max_tokens;
    private int presence_penalty;
    private ResponseFormat response_format;
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
