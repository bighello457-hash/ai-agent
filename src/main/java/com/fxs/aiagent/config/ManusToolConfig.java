package com.fxs.aiagent.config;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class ManusToolConfig {
    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ToolCallbackProvider mcpToolCallbacks;

    @Bean
    public ToolCallback[] manusTool(){
        ToolCallback[] toolCallbacks = mcpToolCallbacks.getToolCallbacks();

        ToolCallback[] combined = Stream.concat(
                Stream.of(allTools),
                Stream.of(toolCallbacks)
        ).toArray(ToolCallback[]::new);
        return combined;
    }
}
