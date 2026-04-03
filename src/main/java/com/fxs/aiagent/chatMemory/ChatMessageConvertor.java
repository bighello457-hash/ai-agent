package com.fxs.aiagent.chatMemory;

import cn.hutool.json.JSONUtil;
import com.fxs.aiagent.domain.ChatMessage;
import org.springframework.ai.chat.messages.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: yu-ai-agent
 * @description:
 * @author: leo
 * @date: 2025-12-01 16:22
 **/
public class ChatMessageConvertor {


    /**
     * message 转换为 ChatMessage
     *
     * @param conversationId
     * @param index
     * @param message
     * @return
     */
    public static ChatMessage toChatMessage(String conversationId, int index, Message message) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversationId(conversationId);
        chatMessage.setMsgIndex(index);
        chatMessage.setContent(message.getText());

        // 确定消息角色
        String role = determineRole(message);
        chatMessage.setRole(role);

        // 保存元数据
        Map<String, Object> metadata = new HashMap<>(message.getMetadata());

        // 如果是 ToolResponseMessage，保存额外信息
        if (message instanceof ToolResponseMessage toolMsg) {
            // Spring AI 1.1.0-M4 版本的 ToolResponseMessage 可能有特殊字段
            // 根据实际情况保存工具相关信息
        }

        if (!metadata.isEmpty()) {
            chatMessage.setMetadata(JSONUtil.toJsonStr(metadata));
        }

        return chatMessage;

    }

    /**
     * ChatMessage 转换为 message
     *
     * @param chatMessage
     * @return
     */
    public static Message toMessage(ChatMessage chatMessage) {
        String role = chatMessage.getRole();
        String content = chatMessage.getContent();

        // 解析 metadata
//        Map<String, Object> metadata = new HashMap<>();
//        if (chatMessage.getMetadata() != null) {
//            String metadataStr = chatMessage.getMetadata().toString();
//            if (JSONUtil.isTypeJSON(metadataStr)) {
//                metadata = JSONUtil.toBean(metadataStr, Map.class);
//            }
//        }

        // 根据角色创建不同类型的 Message
        return switch (role.toLowerCase()) {
            case "system" -> new SystemMessage(content);
            case "user" -> new UserMessage(content);
            case "assistant" -> new AssistantMessage(content);
            default -> new UserMessage(content);
        };

    }

    /**
     * 确定消息的角色类型
     */
    private static String determineRole(Message message) {
        if (message instanceof SystemMessage) {
            return "system";
        } else if (message instanceof UserMessage) {
            return "user";
        } else if (message instanceof AssistantMessage) {
            return "assistant";
        } else if (message instanceof ToolResponseMessage) {
            return "tool";
        } else {
            // 默认为 user
            return "user";
        }
    }
}

