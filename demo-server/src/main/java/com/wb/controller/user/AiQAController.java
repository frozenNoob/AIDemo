package com.wb.controller.user;

import com.wb.dto.AiQADTO;
import com.wb.service.AiQAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aiqa")
public class AiQAController {

    @Autowired
    private AiQAService aiQAService;

    @PostMapping("/chat")
    public String getChatResponse(@RequestBody AiQADTO request) {
        return aiQAService.getChatResponse(request);
    }
}
