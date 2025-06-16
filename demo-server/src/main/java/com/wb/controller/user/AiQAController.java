package com.wb.controller.user;

import com.wb.dto.AiQAByFimDTO;
import com.wb.dto.AiQADTO;
import com.wb.service.AiQAService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/user/api/aiqa")
@Api(tags = "C端用户相关接口")
public class AiQAController {

    @Autowired
    private AiQAService aiQAService;

    @PostMapping("/chat")
    @ApiOperation("用户通过对话补全方式询问AI：DeepSeek")
    public SseEmitter getChatResponse(@RequestBody AiQADTO request) {
        return aiQAService.getChatResponse(request);
    }

    @PostMapping("/chatByFim")
    @ApiOperation("用户通过Fim补全方式询问AI：DeepSeek")
    public SseEmitter getChatResponseByFim(@RequestBody AiQAByFimDTO request) {
        return aiQAService.getChatResponseByFim(request);
    }
}
