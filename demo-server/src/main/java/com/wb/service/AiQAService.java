package com.wb.service;

import com.wb.dto.AiQAByFimDTO;
import com.wb.dto.AiQADTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiQAService {

    /*
    用户通过对话补全方式询问AI
     */
    SseEmitter getChatResponse(AiQADTO request);

    /*
    用户通过Fim方式询问AI
     */
    SseEmitter getChatResponseByFim(AiQAByFimDTO request);
}
