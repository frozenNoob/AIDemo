package com.wb.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将JSON字符串转换为JsonNode对象，支持直接访问属性
     *
     * @param jsonString 合法的JSON格式字符串
     * @return JsonNode对象，可通过.get("属性名")访问数据
     * @throws JsonProcessingException 当JSON格式非法时抛出异常
     */
    public static JsonNode parseToJsonNode(String jsonString) throws JsonProcessingException {
        return objectMapper.readTree(jsonString);
    }

    /**
     * 将JSON字符串映射为对应的Java类对象
     * @param jsonString
     * @param clazz
     * @return
     * @param <T>
     * @throws JsonProcessingException
     */
    public static <T> T parseToEntity(String jsonString, Class<T> clazz) throws JsonProcessingException {
        // 示例代码片段
        return objectMapper.readValue(jsonString, clazz);
    }

    /**
     * 将Java类对象转换为Json字符串
     *
     * @param userInfo
     * @param <T>
     * @return
     */
    public static <T> String convertToJson(T userInfo) {
        try {
            // 将对象（包含嵌套）序列化为JSON字符串
            return objectMapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象转JSON失败", e);
        }
    }
}