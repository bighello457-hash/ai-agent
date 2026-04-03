package com.fxs.aiagent.advisor;

import com.fxs.aiagent.utils.ForbiddenWords;
import org.jetbrains.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

public class ProfanityFilterAdvisor implements CallAdvisor {


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse callAdvisorChain1 = before(chatClientRequest, callAdvisorChain);
        if (callAdvisorChain1 != null) return callAdvisorChain1;

        // 没有检测到违禁词，继续执行链
        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Nullable
    private static ChatClientResponse before(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // 获取用户消息
        String userText = chatClientRequest.prompt().getUserMessage().getText();

        // 检查是否包含违禁词
        if (ForbiddenWords.containsForbiddenWord(userText)) {
            // 创建系统消息指导AI模型返回预设回复
            SystemMessage systemMessage = new SystemMessage(
                    "用户的请求包含不当内容，请直接回复以下内容：\n" +
                            "这类内容不仅不符合公序良俗，还可能传播不良信息，危害身心健康，所以我不能为你提供哦。\n" +
                            "如果你有其他合适的、健康的内容需求，比如聊天、学习知识、规划生活等，我都可以尽力帮你。我们还是多关注积极有益的事情～");

            // 创建新的请求，替换原始用户消息
            ChatClientRequest newRequest = ChatClientRequest.builder()
                    .prompt(Prompt.builder()
                            .messages(List.of(systemMessage))
                            .build())
                    .build();

            // 调用下一个advisor
            return callAdvisorChain.nextCall(newRequest);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}