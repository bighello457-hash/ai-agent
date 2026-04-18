package com.fxs.aiagent.tools;

import com.fxs.aiagent.config.ConsoleInputHolder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class AskHumanTool {
//    @Tool(description = "Ask the user for missing information or feedback before continuing the task")
    public String askHuman(@ToolParam (description = "The question to ask the user")String inquire){
        return "WAITING_HUMAN:" + inquire;
    }
}
