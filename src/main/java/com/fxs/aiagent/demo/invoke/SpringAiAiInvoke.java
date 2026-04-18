package com.fxs.aiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 通过spring ai框架调用ai大模型
 */
@Component
public class SpringAiAiInvoke  {
    /**
     * 之所以能够自动导入，因为我已经在配置文件中配置了就会自动依赖注入一个chatmodel
     */
    @Resource
    private ChatModel dashscopeChatModel;




}
