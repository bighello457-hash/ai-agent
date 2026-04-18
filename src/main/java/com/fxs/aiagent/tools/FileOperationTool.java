package com.fxs.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.fxs.aiagent.constant.FileConstant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class FileOperationTool {
    public static final String FILE_DIR=FileConstant.FILE_SAVE_DIR+"/file";
    @Tool(description = "Read content from a file")
    @McpTool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of the file to read") @McpToolParam(description = "Name of the file to read")String filename){

        String filepath=FILE_DIR+"/"+filename;

        try {
            return FileUtil.readUtf8String(filepath);
        } catch (Exception e) {
            return "Error reading file:"+e.getMessage();
        }
    }

    @Tool(description = "write a content to a file")
    @McpTool(description = "write a content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write")
            @McpToolParam(description = "Name of the file to write")
                                String filename,
                            @ToolParam(description = "Content to write to file")@McpToolParam(description = "Content to write to file")
                            String content){
        String filepath=FILE_DIR+"/"+filename;
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content,filepath);
            return "File written successfully to :"+filepath;
        } catch (Exception e) {
            return "Error write content to a file:"+e.getMessage();
        }
    }
}
