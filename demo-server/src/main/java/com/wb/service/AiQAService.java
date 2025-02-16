package com.wb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wb.dto.AiQADTO;
import com.wb.entity.QAItem;
import com.wb.mapper.AiQAMapper;
import com.wb.utils.JsonUtils;
import com.wb.vo.AiQAVO;
import okhttp3.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class AiQAService {

    private final OkHttpClient client;

    @Autowired
    private AiQAMapper aiQAMapper;
    @Value("${deepseek.api.token}")
    private String apiToken;

    //    TimeUnit.SECONDS 用于表示时间单位。
//    它是 Java 中的一个枚举类型，定义在 java.util.concurrent 包中的 TimeUnit 枚举类中。
    public AiQAService() {
        this.client = new OkHttpClient.Builder().connectTimeout(
                        240, TimeUnit.SECONDS) // 设置连接超时
                .readTimeout(240, TimeUnit.SECONDS)    // 设置读取超时
                .writeTimeout(240, TimeUnit.SECONDS)   // 设置写入超时
                .build();
    }

    public AiQAVO getChatResponse(AiQADTO request) {
        MediaType mediaType = MediaType.parse("application/json");
//        request.toJson()能够得到一个JSON格式的字符串（这个JSON格式是可以类对象的格式来的，
//        比如类A中有类B，那么类B是一个子JSON字符串。
//        类B是个数组形式的话，那么就是一个列表形式的JSON字符串。
        RequestBody body = RequestBody.create(request.toJson(), mediaType);//json字符串作为第一个参数
        Request req = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        try (Response response = client.newCall(req).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                String res = response.body().string();//用完之后就会关闭链接，此时response已经不能使用了
                System.out.println("当前用户问了DeepSeek后的回答为：\n" + res);

                JsonNode node = JsonUtils.parseToJsonNode(res);
                String aiAnswer = node.get("choices")
                        .get(0) // 获取第一个元素，因为choices 是一个数组
                        .get("message")
                        .get("content")
                        .asText();
                String prompt_tokens = node.get("usage")
                        .get("prompt_tokens").asText();
                String completion_tokens = node.get("usage")
                        .get("completion_tokens").asText();

                AiQAVO aiQAVO = AiQAVO.of(aiAnswer, Integer.parseInt(prompt_tokens)
                        , Integer.parseInt(completion_tokens));
                // 插入到数据库
                QAItem qaItem = new QAItem();
                BeanUtils.copyProperties(aiQAVO, qaItem);
                qaItem.setQuestion(request.getMessages()[0].getContent());//存储问题
                aiQAMapper.insert(qaItem);
                return aiQAVO;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
