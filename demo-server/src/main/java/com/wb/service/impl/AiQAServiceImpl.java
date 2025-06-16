package com.wb.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.wb.context.BaseContext;
import com.wb.dto.AiQAByFimDTO;
import com.wb.dto.AiQADTO;
import com.wb.entity.QAItem;
import com.wb.mapper.AiQAMapper;
import com.wb.service.AiQAService;
import com.wb.utils.JsonUtils;
import com.wb.vo.AiQAVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AiQAServiceImpl implements AiQAService {

    private final OkHttpClient client;
    private Long userId;
    @Autowired
    private AiQAMapper aiQAMapper;

    @Value("${deepseek.api.token}")
    private String apiToken;

    //    TimeUnit.SECONDS 用于表示时间单位。
//    它是 Java 中的一个枚举类型，定义在 java.util.concurrent 包中的 TimeUnit 枚举类中。
    public AiQAServiceImpl() {
        this.client = new OkHttpClient.Builder().connectTimeout(
                        240, TimeUnit.SECONDS) // 设置连接超时
                .readTimeout(240, TimeUnit.SECONDS)    // 设置读取超时
                .writeTimeout(240, TimeUnit.SECONDS)   // 设置写入超时
                .build();
    }

    public SseEmitter getChatResponse(AiQADTO aiQADTO) {
        MediaType mediaType = MediaType.parse("application/json");
//        aiQADTO.toJson()能够得到一个JSON格式的字符串（这个JSON格式是可以类对象的格式来的，
//        比如类A中有类B，那么类B是一个子JSON字符串。
//        类B是个数组形式的话，那么就是一个列表形式的JSON字符串。
        RequestBody body = RequestBody.create(aiQADTO.toJson(), mediaType);//json字符串作为第一个参数
        Request req = new Request.Builder()
                .url("https://api.deepseek.com/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        // 得到流式/非流式回答
        SseEmitter emitter = new SseEmitter(0L); // 0L表示永不超时
        // 获取用户ID
        userId = BaseContext.getCurrentId();
        // 不是流式传输
        if (!aiQADTO.isStream()) {
            try (Response response = client.newCall(req).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();//用完之后就会关闭链接，此时response已经不能使用了
                    System.out.println("当前用户问了DeepSeek后的回答为：\n" + res);
                    AiQAVO aiQAVO = addressJson(res, false);
                    emitter.send(aiQAVO.getAnswer());//这里只发送回答
                    emitter.complete();
                    insertQA2Schema(aiQAVO, aiQADTO);
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                log.error(e.toString());
                emitter.completeWithError(e);
            }
        } else {// 流式传输
        /*
            Server-Sent Events（SSE）是一种用于实现服务器向客户端实时推送数据的Web技术。
            与传统的轮询和长轮询相比，SSE提供了更高效和实时的数据推送机制。
            SSE基于HTTP协议，允许服务器将数据以事件流（Event Stream）的形式发送给客户端。
         */
            //OkHttp库的client.newCall(req).enqueue(new Callback() {...})方法，它会在后台异步执行网络请求。
            // 这意味着在调用enqueue时，OkHttp会创建一个新的线程或利用线程池中的线程来处理这个请求，而不是在主线程中执行。
            //调用该函数的时候，相当于开了个新的高优先级线程来发送请求。所以主线程ThreadLocal中设置的Id无用。
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error(e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        StringBuilder ansBuffer = new StringBuilder();
                        String jsonData = null; //记录访问API的回答
                        AiQAVO aiQAVO = null;
                        System.out.println("AI返回的回答如下：");
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();//按行读取
                            if (line == null) break;
                            else if (line.equals("data: [DONE]")) { // [DONE]
                                System.out.println("成功结束本次AI问答");
                                aiQAVO.setAnswer(ansBuffer.toString());
                                // 记录到数据库
                                System.out.println("记录到数据库中");
                                insertQA2Schema(aiQAVO, aiQADTO);
                                emitter.complete();
                                break;
                            } else if (line.startsWith("data: ")) {
                                // 提取 JSON 数据（去除 "data: " 前缀）
                                jsonData = line.substring(6).trim();
                                if (!jsonData.isEmpty()) {
                                    System.out.println(jsonData);
                                    aiQAVO = addressJson(jsonData, true);
                                    ansBuffer.append(aiQAVO.getAnswer());
                                    // 这个会把线程的userId去掉！
                                    emitter.send(aiQAVO.getAnswer());//通过SSE技术实时发送回答给客户端。
                                }
                            } else { // DeepSeek R1返回的结果会以1个空字符为结尾！！
                                System.out.println("读取到空数据");
                            }
                        }
                    }
                }
            });
        }

        System.out.println("newCall还未结束");//OkHttp库的异步调用。
        return emitter;
    }

    @Override
    public SseEmitter getChatResponseByFim(AiQAByFimDTO aiQADTO) {
        MediaType mediaType = MediaType.parse("application/json");
//        aiQADTO.toJson()能够得到一个JSON格式的字符串（这个JSON格式是可以类对象的格式来的，
//        比如类A中有类B，那么类B是一个子JSON字符串。
//        类B是个数组形式的话，那么就是一个列表形式的JSON字符串。
        RequestBody body = RequestBody.create(aiQADTO.toJson(), mediaType);//json字符串作为第一个参数
        Request req = new Request.Builder()
                .url("https://api.deepseek.com/beta/completions") // 对于FIM补全来说，路径中这个是beta。
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiToken)
                .build();

        // 得到流式/非流式回答
        SseEmitter emitter = new SseEmitter(0L); // 0L表示永不超时
        // 获取用户ID
        userId = BaseContext.getCurrentId();
        // 不是流式传输
        if (!aiQADTO.isStream()) {
            try (Response response = client.newCall(req).execute()) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String res = response.body().string();//用完之后就会关闭链接，此时response已经不能使用了
                    System.out.println("当前用户问了DeepSeek后的回答为：\n" + res);
                    AiQAVO aiQAVO = addressJsonForFim(res, false);
                    emitter.send(aiQAVO.getAnswer());//这里只发送回答
                    emitter.complete();
                    insertQA2Schema(aiQAVO, aiQADTO);
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            } catch (IOException e) {
                log.error(e.toString());
                emitter.completeWithError(e);
            }
        }
        else {// 流式传输
        /*
            Server-Sent Events（SSE）是一种用于实现服务器向客户端实时推送数据的Web技术。
            与传统的轮询和长轮询相比，SSE提供了更高效和实时的数据推送机制。
            SSE基于HTTP协议，允许服务器将数据以事件流（Event Stream）的形式发送给客户端。
         */
            //OkHttp库的client.newCall(req).enqueue(new Callback() {...})方法，它会在后台异步执行网络请求。
            // 这意味着在调用enqueue时，OkHttp会创建一个新的线程或利用线程池中的线程来处理这个请求，而不是在主线程中执行。
            //调用该函数的时候，相当于开了个新的高优先级线程来发送请求。所以主线程ThreadLocal中设置的Id无用。
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    log.error(e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        BufferedSource source = body.source();
                        StringBuilder ansBuffer = new StringBuilder();
                        String jsonData = null; //记录访问API的回答
                        AiQAVO aiQAVO = null;
                        System.out.println("AI返回的回答如下：");
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();//按行读取
                            if (line == null) break;
                            else if (line.equals("data: [DONE]")) { // [DONE]
                                System.out.println("成功结束本次AI问答");
                                aiQAVO.setAnswer(ansBuffer.toString());
                                // 记录到数据库
                                System.out.println("记录到数据库中");
                                insertQA2Schema(aiQAVO, aiQADTO);
                                emitter.complete();
                                break;
                            } else if (line.startsWith("data: ")) {
                                // 提取 JSON 数据（去除 "data: " 前缀）
                                jsonData = line.substring(6).trim();
                                if (!jsonData.isEmpty()) {
                                    System.out.println(jsonData);
                                    aiQAVO = addressJsonForFim(jsonData, true);
                                    ansBuffer.append(aiQAVO.getAnswer());
                                    // 这个会把线程的userId去掉！
                                    emitter.send(aiQAVO.getAnswer());//通过SSE技术实时发送回答给客户端。
                                }
                            } else { // DeepSeek R1返回的结果会以1个空字符为结尾！！
                                System.out.println("读取到空数据");
                            }
                        }
                    }
                }
            });
        }

        System.out.println("newCall还未结束");//OkHttp库的异步调用。
        return emitter;
    }

    // 处理响应得到的Json格式的字符串，并插入到数据库
    public AiQAVO addressJson(String response, Boolean isStream) throws JsonProcessingException {
        JsonNode node = JsonUtils.parseToJsonNode(response);
        String aiAnswer;
        if (!isStream) {
            aiAnswer = node.get("choices")
                    .get(0) // 获取第一个元素，因为choices 是一个数组
                    .get("message")
                    .get("content")
                    .asText();
        } else {// 流式响应
            aiAnswer = node.get("choices")
                    .get(0) //
                    .get("delta")
                    .get("content")
                    .asText();
        }
        // 如果是流式响应的话，一般是倒数第二行，即[DONE]这最后一行的前面
        if (node.get("usage") == null) {
            return AiQAVO.builder().answer(aiAnswer).build();
        }

        String prompt_tokens = node.get("usage")
                .get("prompt_tokens").asText();
        String completion_tokens = node.get("usage")
                .get("completion_tokens").asText();

        AiQAVO aiQAVO = AiQAVO.of(aiAnswer, Integer.parseInt(prompt_tokens)
                , Integer.parseInt(completion_tokens));
        return aiQAVO;
    }


    // 处理响应得到的Json格式的字符串，并插入到数据库
    public AiQAVO addressJsonForFim(String response, Boolean isStream) throws JsonProcessingException {
        JsonNode node = JsonUtils.parseToJsonNode(response);
        String aiAnswer;
        if (!isStream) {
            aiAnswer = node.get("choices")
                    .get(0) // 获取第一个元素，因为choices 是一个数组
                    .get("text")
                    .asText();
        } else {// 流式响应
            aiAnswer = node.get("choices")
                    .get(0) //
                    .get("text")
                    .asText();
        }
        // 如果是流式响应的话，一般是倒数第二行，即[DONE]这最后一行的前面
        if (node.get("usage") == null) {
            return AiQAVO.builder().answer(aiAnswer).build();
        }

        String prompt_tokens = node.get("usage")
                .get("prompt_tokens").asText();
        String completion_tokens = node.get("usage")
                .get("completion_tokens").asText();

        AiQAVO aiQAVO = AiQAVO.of(aiAnswer, Integer.parseInt(prompt_tokens)
                , Integer.parseInt(completion_tokens));
        return aiQAVO;
    }



    public void insertQA2Schema(AiQAVO aiQAVO, AiQADTO aiQADTO) {
        // 插入到数据库
        QAItem qaItem = new QAItem();
        BeanUtils.copyProperties(aiQAVO, qaItem);
        qaItem.setUserId(userId);//从线程中获取到用户Id（本次线程中拦截器进行登录校验时已经设置好了）
        qaItem.setQuestion(aiQADTO.getMessages()[0].getContent());//存储问题
        aiQAMapper.insert(qaItem);
    }

    public void insertQA2Schema(AiQAVO aiQAVO, AiQAByFimDTO aiQADTO) {
        // 插入到数据库
        QAItem qaItem = new QAItem();
        BeanUtils.copyProperties(aiQAVO, qaItem);
        qaItem.setUserId(userId);//从线程中获取到用户Id（本次线程中拦截器进行登录校验时已经设置好了）
        qaItem.setQuestion("采用了FIM补全的方式进行AI问答：\n"+aiQADTO.getPrompt()+"\n后缀为："+aiQADTO.getSuffix());//存储问题
        aiQAMapper.insert(qaItem);
    }
}
