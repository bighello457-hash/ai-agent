package com.fxs.aiagent.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * AI 会话消息表
 * @TableName chat_message
 */
@TableName(value ="chat_message")
@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 8232352933314809301L;
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "conversation_id")
    private String conversationId;

    /**
     * 消息在会话中的顺序（从0开始，可用于排序）
     */
    @TableField(value = "msg_index")
    private Integer msgIndex;
    /**
     * 消息角色：system/user/assistant/tool/function 等
     */
    private String role;

    /**
     * 消息内容（文本或JSON字符串）
     */
    private String content;

    /**
     * 额外元数据，例如工具调用参数、function call、模型信息等
     */
    private Object metadata;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}