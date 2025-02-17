package com.wb.controller.user;

import com.wb.dto.AiQADTO;
import com.wb.service.AiQAService;
import com.wb.vo.AiQAVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aiqa")
@Api(tags = "C端用户相关接口")
public class AiQAController {

    @Autowired
    private AiQAService aiQAService;

    @PostMapping("/chat")
    @ApiOperation("用户询问AI：DeepSeek")
    public AiQAVO getChatResponse(@RequestBody AiQADTO request) {
        AiQAVO ans = aiQAService.getChatResponse(request);

        // 因为@ResponseBody注解能够把返回的对象封装为JSON格式，所以这里不需要额外的转换！
        //return ans.toJson();
        return ans;
    }
}
