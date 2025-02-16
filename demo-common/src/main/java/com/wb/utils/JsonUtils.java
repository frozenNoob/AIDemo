package com.wb.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON字符串转换为JsonNode对象，支持直接访问属性
     * @param jsonString 合法的JSON格式字符串
     * @return JsonNode对象，可通过.get("属性名")访问数据
     * @throws JsonProcessingException 当JSON格式非法时抛出异常
     */
    public static JsonNode parseToJsonNode(String jsonString) throws JsonProcessingException {
        return objectMapper.readTree(jsonString);
    }
}