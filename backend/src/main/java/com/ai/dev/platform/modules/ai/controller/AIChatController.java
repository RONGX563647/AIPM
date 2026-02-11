package com.ai.dev.platform.modules.ai.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.ai.service.AIChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI聊天", description = "AI聊天接口")
@RestController
@RequestMapping("/ai/chat")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    @Operation(summary = "发送消息给AI")
    @PostMapping("/send")
    public Result<String> sendMessage(@RequestBody ChatRequest request) {
        try {
            String response = aiChatService.chat(request.getMessage());
            return Result.ok(response);
        } catch (Exception e) {
            return Result.error("AI聊天失败：" + e.getMessage());
        }
    }

    public static class ChatRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
