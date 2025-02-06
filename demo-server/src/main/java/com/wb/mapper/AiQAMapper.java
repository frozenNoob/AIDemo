package com.wb.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AiQAMapper {

    private final Message[] messages;
    private final String model;
    private final int frequency_penalty;
    private final int max_tokens;
    private final int presence_penalty;
    private final ResponseFormat response_format;
    private final boolean stream;
    private final double temperature;
    private final double top_p;

    // Constructor
    public AiQAMapper(Message[] messages, String model, int frequency_penalty, int max_tokens,
                       int presence_penalty, ResponseFormat response_format, boolean stream,
                       double temperature, double top_p) {
        this.messages = messages;
        this.model = model;
        this.frequency_penalty = frequency_penalty;
        this.max_tokens = max_tokens;
        this.presence_penalty = presence_penalty;
        this.response_format = response_format;
        this.stream = stream;
        this.temperature = temperature;
        this.top_p = top_p;
    }

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
    public static class Message {
        private final String content;
        private final String role;

        public Message(String content, String role) {
            this.content = content;
            this.role = role;
        }

        // Getters or toString(), if needed
    }

    // Inner class for response format
    public static class ResponseFormat {
        private final String type;

        public ResponseFormat(String type) {
            this.type = type;
        }

        // Getters or toString(), if needed
    }
}