package com.fxs.aiagent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxs.aiagent.domain.ChatMessage;
import com.fxs.aiagent.service.ChatMessageService;
import com.fxs.aiagent.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author 24352
* @description 针对表【chat_message(AI 会话消息表)】的数据库操作Service实现
* @createDate 2026-04-03 22:20:23
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




