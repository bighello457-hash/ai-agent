package com.fxs.aiagent.chatMemory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fxs.aiagent.domain.ChatMessage;
import com.fxs.aiagent.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: yu-ai-agent
 * @description:
 * @author: leo
 * @date: 2025-12-01 13:08
 **/
@Component
public class MysqlBasedChatMemoryRepository implements ChatMemoryRepository {

    @Resource
    private ChatMessageMapper chatMessageMapper;

    /**
     * 查找所有的会话 id
     *
     * @return
     */
    @Override
    public List<String> findConversationIds() {
        return chatMessageMapper.selectList(
                        new QueryWrapper<ChatMessage>()
                                .select("distinct conversation_id")
                ).stream()
                .map(ChatMessage::getConversationId)
                .distinct()
                .toList();
    }

    /**
     * 根据 conversationId 查找指定的会话消息
     *
     * @param conversationId
     * @return
     */
    @Override
    public List<Message> findByConversationId(String conversationId) {

        List<ChatMessage> list = chatMessageMapper.selectList(
                new QueryWrapper<ChatMessage>()
                        .eq("conversation_id", conversationId)
                        .orderByAsc("msg_index")
        );

        return list.stream()
                .map(ChatMessageConvertor::toMessage)
                .collect(Collectors.toList());

    }

    /**
     * 保存所有的会话消息
     *
     * @param conversationId
     * @param messages
     */
    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        deleteByConversationId(conversationId);
        int index = 0;
        for (Message message : messages) {
            ChatMessage chatMessage = ChatMessageConvertor.toChatMessage(conversationId, index++, message);
            chatMessageMapper.insert(chatMessage);
        }
    }

    /**
     * 通过 会话id删除会话
     *
     * @param conversationId
     */
    @Override
    public void deleteByConversationId(String conversationId) {
        chatMessageMapper.delete(
                new QueryWrapper<ChatMessage>()
                        .eq("conversation_id", conversationId)
        );
    }
}
