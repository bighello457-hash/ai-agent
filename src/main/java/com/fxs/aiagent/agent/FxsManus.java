package com.fxs.aiagent.agent;

import com.fxs.aiagent.advisor.myLogAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class FxsManus extends ToolCallAgent{

    public FxsManus(@Qualifier("manusTool") ToolCallback[] manusTool, ChatModel dashscopeChatModel){
        super(manusTool);
        this.setName("FxsManus");
        String SYSTEM_PROMPT = """  
                You are FxsManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                 
              
                """;
        String complete="""  
                You are FxsManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                 
                If required information is missing, you MUST call the `askHuman` tool.
                               Do NOT ask the user follow-up questions in plain assistant text.
                               Do NOT continue by guessing missing parameters.
                               Ask exactly one concise question through the `askHuman` tool each time.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(10);
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new myLogAdvisor())
                .build();
        this.setChatClient(chatClient);
    }
}
