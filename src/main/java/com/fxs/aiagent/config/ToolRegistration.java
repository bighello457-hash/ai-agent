package com.fxs.aiagent.config;

import com.fxs.aiagent.tools.*;
import org.springframework.ai.mcp.McpToolUtils;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {


    @Bean
    public ToolCallback[] allTools() {


        FileOperationTool fileOperationTool = new FileOperationTool();
        AskHumanTool askHumanTool=new AskHumanTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        WebSearchTool webSearchTool=new WebSearchTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool=new TerminateTool();
        return ToolCallbacks.from(
                fileOperationTool,
//                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool
//                askHumanTool
        );
    }
}

