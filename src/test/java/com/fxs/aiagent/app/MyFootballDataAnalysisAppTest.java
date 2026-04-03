package com.fxs.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyFootballDataAnalysisAppTest {
    @Resource
    private MyFootballDataAnalysisApp myFootballDataAnalysisApp;
    @Test
    void doChat() {
        String text="你好我是傅星顺";
        String chatId = UUID.randomUUID().toString();
        myFootballDataAnalysisApp.doChat(text,chatId);
    }
}