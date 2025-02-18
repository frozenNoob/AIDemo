package com.wb.service;

import com.wb.dto.AiQADTO;
import com.wb.vo.AiQAVO;

public interface AiQAService {

    /*
    用户询问AI
     */
    AiQAVO getChatResponse(AiQADTO request);
}
