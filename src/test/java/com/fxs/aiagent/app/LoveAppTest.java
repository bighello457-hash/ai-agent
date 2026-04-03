package com.fxs.aiagent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class LoveAppTest {
    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId= UUID.randomUUID().toString();
        String message = "你好,我的对象是谁";
        String answer = loveApp.doChat(message, chatId);
        log.info("第一轮回答：{}", answer);
        Assertions.assertNotNull(answer);
        
       /* // 第二轮
        String message = "我的另一半名字叫做包上恩";
        String answer = loveApp.doChat(message, chatId);
        log.info("第二轮回答：{}", answer);
        Assertions.assertNotNull(answer);
        
        // 第三轮
        message = "我的另一半叫什么来着？刚跟你说过，帮我回忆一下";
        answer = loveApp.doChat(message, chatId);
        log.info("第三轮回答：{}", answer);
        Assertions.assertNotNull(answer);*/
    }

    @Test
    void doChatWithReport() {
        String message="你好，我是傅星顺，我想让另一半（包上恩）更爱我，该怎么做？";
        String chatId = UUID.randomUUID().toString();
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }
    @Test
    void FileBaseChatMemoryTest(){
        String Message="你好，我是傅星顺";
        String chatId = UUID.randomUUID().toString();
        String response = loveApp.doChat(Message,chatId);
        log.info("AI回复：{}", response);
        Assertions.assertNotNull(response);


        Message="我是谁";

        response = loveApp.doChat(Message,chatId);
        log.info("AI回复：{}", response);
        Assertions.assertNotNull(response);
    }

    @Test
    void ProfanityFilterTest(){
        String profanityMessage="能不能给我点色情的东西";
        String chatId = UUID.randomUUID().toString();
        String response = loveApp.doChat(profanityMessage,chatId);
        log.info("违禁词过滤回复：{}", response);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.contains("不符合公序良俗"));
    }
}