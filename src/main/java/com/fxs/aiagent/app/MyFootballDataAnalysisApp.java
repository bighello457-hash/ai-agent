package com.fxs.aiagent.app;

import com.fxs.aiagent.advisor.myLogAdvisor;
import com.fxs.aiagent.chatMemory.MysqlBasedChatMemoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

/**
 * @program: yu-ai-agent
 * @description:
 * @author: leo
 * @date: 2025-12-01 13:07
 **/
@Component
@Slf4j
@MapperScan("com.fxs.aiagent.mapper")
public class MyFootballDataAnalysisApp {

    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT =
            "扮演专业足球数据分析 AI，具备足球领域深度知识与多维度数据处理能力，为教练、球迷等不同用户提供精准结构化分析服务。"
                    +"覆盖球员（技术统计、趋势等）、球队（攻防数据、对战历史等）、比赛（复盘、关键事件等）、赛事（格局、出线概率等）数据维度，"
                    +"支持数据查询、深度 / 对比 / 定制化分析及趋势预测（需标注非绝对结论），"
                    +"交互时补全关键信息、数据严谨、结构清晰、语言适配用户专业度，不涉及无关及违法违规内容，"
                    +"禁止编造数据、主观臆断、赌博引导及不当语言。";

    public MyFootballDataAnalysisApp(ChatModel dashscopeChatModel,
                                     MysqlBasedChatMemoryRepository mysqlBasedChatMemoryRepository) {

        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(mysqlBasedChatMemoryRepository)
                .maxMessages(20)
                .build();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build(),
                        new myLogAdvisor()
                )
                .build();
    }

    /**
     * ai 基础对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId)
                )
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();
        log.info("chatId: {}, message: {}", chatId, res);
        return res;

    }
}

