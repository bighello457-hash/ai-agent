package com.fxs.aiagent.app;

import com.fxs.aiagent.advisor.myLogAdvisor;
import com.fxs.aiagent.chatMemory.MysqlBasedChatMemoryRepository;
import com.fxs.aiagent.prompt.PromptTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用模板化Prompt的足球数据分析应用
 *
 * @author fxs
 */
@Component
@Slf4j
public class FootballAnalysisApp {

    private final ChatClient chatClient;
    private final ChatModel chatModel;
    private final PromptTemplateService promptTemplateService;

    public FootballAnalysisApp(ChatModel dashscopeChatModel,
                               MysqlBasedChatMemoryRepository mysqlBasedChatMemoryRepository,
                               PromptTemplateService promptTemplateService) {
        this.chatModel = dashscopeChatModel;
        this.promptTemplateService = promptTemplateService;

        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(mysqlBasedChatMemoryRepository)
                .maxMessages(20)
                .build();

        // 使用模板服务加载系统提示
        String systemPrompt = buildSystemPrompt("教练", "战术分析与球员表现", "专业严谨");

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(memory).build(),
                        new myLogAdvisor()
                )
                .build();
    }

    /**
     * 构建系统提示
     *
     * @param userType      用户类型（教练、球迷、记者等）
     * @param focusArea     分析重点
     * @param languageStyle 语言风格
     * @return 渲染后的系统提示
     */
    private String buildSystemPrompt(String userType, String focusArea, String languageStyle) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userType", userType);
        variables.put("focusArea", focusArea);
        variables.put("languageStyle", languageStyle);

        return promptTemplateService.render("football-analysis.st", variables);
    }

    /**
     * 动态切换系统提示（重新构建ChatClient）
     *
     * @param userType      用户类型
     * @param focusArea     分析重点
     * @param languageStyle 语言风格
     */
    public ChatClient createChatClient(String userType, String focusArea, String languageStyle) {
        String systemPrompt = buildSystemPrompt(userType, focusArea, languageStyle);

        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .build();
    }

    /**
     * AI基础对话
     *
     * @param message 用户消息
     * @param chatId  会话ID
     * @return AI响应
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();
        log.info("chatId: {}, response: {}", chatId, res);
        return res;
    }

    /**
     * 带动态系统提示的对话
     *
     * @param message       用户消息
     * @param chatId        会话ID
     * @param userType      用户类型
     * @param focusArea     分析重点
     * @param languageStyle 语言风格
     * @return AI响应
     */
    public String doChatWithDynamicPrompt(String message, String chatId,
                                          String userType, String focusArea, String languageStyle) {
        String systemPrompt = buildSystemPrompt(userType, focusArea, languageStyle);

        ChatResponse chatResponse = chatClient
                .prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();

        String res = chatResponse.getResult().getOutput().getText();
        log.info("chatId: {}, response: {}", chatId, res);
        return res;
    }
}
