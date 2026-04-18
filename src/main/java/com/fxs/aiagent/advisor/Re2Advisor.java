package com.fxs.aiagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.Map;

public class Re2Advisor implements CallAdvisor, StreamAdvisor {

    private static final String RE2_TEMPLATE = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // 1. 获取用户原始问题
        ChatClientRequest newRequest = before(chatClientRequest);

        // 4. 继续执行链
        return callAdvisorChain.nextCall(newRequest);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 流式处理逻辑类似
        ChatClientRequest newRequest = before(chatClientRequest);

        return streamAdvisorChain.nextStream(newRequest);
    }

    @NotNull
    private static ChatClientRequest before(ChatClientRequest chatClientRequest) {
        String userText = chatClientRequest.prompt().getUserMessage().getText();

        String augmentedText = PromptTemplate.builder()
                .template(RE2_TEMPLATE)
                .variables(Map.of("re2_input_query", userText))
                .build()
                .render();

        ChatClientRequest newRequest = chatClientRequest.mutate()
                .prompt(chatClientRequest.prompt().augmentUserMessage(augmentedText))
                .build();
        return newRequest;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
