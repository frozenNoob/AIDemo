package com.wb.service;

import com.wb.dto.AiQADTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiQAService {

    /*
    用户询问AI
     */
    SseEmitter getChatResponse(AiQADTO request);
}
