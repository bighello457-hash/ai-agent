package com.fxs.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;

@Slf4j
public class TimeAdvisor implements CallAdvisor, StreamAdvisor {
    private static final String START_TIME_KEY = "time_advisor_start_time";

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        before(chatClientRequest);
        try {
            return callAdvisorChain.nextCall(chatClientRequest);
        } finally {
            after(chatClientRequest, "call");
        }
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        before(chatClientRequest);
        return streamAdvisorChain.nextStream(chatClientRequest)
                .doFinally(signalType -> {
                    after(chatClientRequest, "stream:" + signalType);
                });
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * Store start time in adviseContext so other advisors can also read it.
     */
    private void before(ChatClientRequest request) {
        request.context().put(START_TIME_KEY, System.currentTimeMillis());
    }

    private void after(ChatClientRequest request, String phase) {
        Object value = request.context().remove(START_TIME_KEY);
        if (value instanceof Long startTime) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("[TimeAdvisor] {} finished, elapsed={}ms", phase, elapsed);
        } else {
            log.warn("[TimeAdvisor] {} finished, but start time missing in adviseContext", phase);
        }
    }
}
