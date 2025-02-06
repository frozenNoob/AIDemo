package com.wb.service;

import com.wb.dto.AiQADTO;
import com.wb.mapper.AiQAMapper;
import okhttp3.*;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AiQAService {

    private final OkHttpClient client;

    @Value("${deepseek.api.token}")
    private String apiToken;

//    TimeUnit.SECONDS 用于表示时间单位。
//    它是 Java 中的一个枚举类型，定义在 java.util.concurrent 包中的 TimeUnit 枚举类中。
    public AiQAService() {
        this.client = new OkHttpClient.Builder().connectTimeout(
                30, TimeUnit.SECONDS) // 设置连接超时
                .readTimeout(30, TimeUnit.SECONDS)    // 设置读取超时
                .writeTimeout(30, TimeUnit.SECONDS)   // 设置写入超时
                .build();
    }

    public String getChatResponse(AiQADTO request) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, request.toJson());
        Request req = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        try (Response response = client.newCall(req).execute()) {
            if (response.isSuccessful()) {
                String answer = response.body().string();//用完之后就会关闭链接，此时response已经不能使用了
                System.out.println("当前用户问了DeepSeek后的回答为：\n" + answer);
                return answer;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
