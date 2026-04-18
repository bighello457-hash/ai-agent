package com.fxs.aiagent.controller;

import com.fxs.aiagent.agent.FxsManus;
import com.fxs.aiagent.app.LoveApp;
import com.fxs.aiagent.app.OfferPilot;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private OfferPilot offerPilot;
    @Resource
    @Qualifier("manusTool")
    private ToolCallback[] manusTools;
    @Resource
    private ChatModel dashScopeChatModel;

    /**
     * 同步接口
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message,String chatId){
         return offerPilot.doChat(message,chatId);
    }


    @GetMapping(value = "love_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message,String chatId){
        return offerPilot.doChatByStream(message, chatId);
    }
    @GetMapping("/love_app/chat/sse/server")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message,String chatId){
        return offerPilot.doChatByStream(message, chatId)
                .map(chunk-> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
    @GetMapping("/love_app/chat/sse/emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message,String chatId){
        SseEmitter emitter=new SseEmitter(180000L);
        offerPilot.doChatByStream(message,chatId)
                .subscribe(
                        chunk-> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        emitter::completeWithError,
                        emitter::complete
                );
        return emitter;

    }


    @GetMapping(value = "/manus/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter doChatWithManus(String message){
        FxsManus fxsManus=new FxsManus(manusTools,dashScopeChatModel);
        SseEmitter sseEmitter = fxsManus.runStream(message);
        return sseEmitter;
    }



}
