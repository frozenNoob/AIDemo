package com.wb.controller.user;

import com.wb.dto.AiQADTO;
import com.wb.service.AiQAService;
import com.wb.vo.AiQAVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aiqa")
public class AiQAController {

    @Autowired
    private AiQAService aiQAService;

    @PostMapping("/chat")
    public String getChatResponse(@RequestBody AiQADTO request) {
        AiQAVO ans = aiQAService.getChatResponse(request);

        return ans.toJson();
    }
}
