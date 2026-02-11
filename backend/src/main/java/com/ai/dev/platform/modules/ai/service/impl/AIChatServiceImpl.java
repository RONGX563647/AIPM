package com.ai.dev.platform.modules.ai.service.impl;

import com.ai.dev.platform.modules.ai.service.AIChatService;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatServiceImpl implements AIChatService {

    private static final Logger logger = LoggerFactory.getLogger(AIChatServiceImpl.class);

    @Value("${dashscope.api-key:}")
    private String apiKey;

    @Override
    public String chat(String message) {
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("DashScope API key not configured");
            return "抱歉，AI服务未正确配置，请联系管理员。";
        }

        try {
            Generation gen = new Generation();
            
            List<Message> messages = new ArrayList<>();
            messages.add(Message.builder()
                    .role(Role.USER.getValue())
                    .content(message)
                    .build());

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model("qwen-plus")
                    .messages(messages)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            GenerationResult result = gen.call(param);
            
            if (result != null && result.getOutput() != null && 
                result.getOutput().getChoices() != null && 
                !result.getOutput().getChoices().isEmpty()) {
                String response = result.getOutput().getChoices().get(0).getMessage().getContent();
                logger.info("AI chat response received successfully");
                return response;
            } else {
                logger.warn("AI chat response is empty");
                return "抱歉，AI暂时无法回答这个问题。";
            }
            
        } catch (NoApiKeyException e) {
            logger.error("No API key exception", e);
            return "抱歉，AI服务配置错误：缺少API密钥。";
        } catch (ApiException e) {
            logger.error("API exception", e);
            return "抱歉，AI服务调用失败：" + e.getMessage();
        } catch (InputRequiredException e) {
            logger.error("Input required exception", e);
            return "抱歉，AI服务调用失败：缺少必要参数。";
        } catch (Exception e) {
            logger.error("Unexpected error in AI chat", e);
            return "抱歉，AI服务发生错误，请稍后再试。";
        }
    }
}
