package com.fxs.aiagent.agent;

import com.fxs.aiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 抽象基础代理类，用于管理代理状态和执行流程
 *
 * 提供状态转换，内存管理，基于步骤的执行循环的基础功能
 * 子类必须实现step方法
 */
@Data
@Slf4j
public abstract class BaseAgent {
    /**
     * 核心属性
     */
    private String name;
    /**
     * 系统提示词
     */
    private String systemPrompt;
    /**
     * 下一步提示词
     */
    private String nextStepPrompt;
    /**
     * 状态（初始为空闲状态）
     */
    private AgentState state=AgentState.IDLE;
    /**
     * 最大循环次数
     */
    private int maxSteps=10;
    /**
     * 当前循环次数
     */
    private int currentStep=0;
    /**
     * 调用的LLM
     */
    private ChatClient chatClient;
    /**
     * 自主维护会话上下文
     */
    private List<Message> messageList=new ArrayList<>();

    /**
     * 防止循环阈值设定
     */
    private int duplicateThreshold=2;


    public String run(String userPrompt){
        if(this.state!=AgentState.IDLE)throw new RuntimeException("Cannot run agent from state :"+this.state);
        if(StringUtils.isBlank(userPrompt))throw new RuntimeException("Cannot run agent with empty user prompt");

        state=AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        //记录结果列表
        List<String>results=new ArrayList<>();

        try {
            for(int i=0;i<maxSteps&&state!=AgentState.FINISHED;i++){
                int stepNumber=i+1;
                currentStep=stepNumber;
                log.info("执行步数："+currentStep+"/"+maxSteps);

                String stepResult=step();
                if(isStuck())handStuckState();
                String result="Step"+currentStep+":"+stepResult;
                results.add(result);
            }
            if(currentStep>=maxSteps){
                state=AgentState.FINISHED;
                results.add("Terminated: Reached max step("+maxSteps+")");
            }
            return String.join("\n",results);
        } catch (Exception e) {
            state=AgentState.ERROR;
            log.error("执行agent失败",e);
            return "执行错误"+e.getMessage();
        }finally {
            this.cleanUp();
        }


    }


    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter=new SseEmitter(300000L);
        CompletableFuture.runAsync(()->{
            try {
                if(this.state!=AgentState.IDLE){
                    sseEmitter.send("Cannot run agent from state :"+this.state);
                    sseEmitter.complete();
                    return ;
                }
                if(StringUtils.isBlank(userPrompt)){
                    sseEmitter.send("Cannot run agent with empty user prompt");
                    sseEmitter.complete();
                    return ;
                }
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }

            state=AgentState.RUNNING;
            messageList.add(new UserMessage(userPrompt));
            //记录结果列表
            List<String>results=new ArrayList<>();

            try {
                for(int i=0;i<maxSteps&&state!=AgentState.FINISHED;i++){
                    int stepNumber=i+1;
                    currentStep=stepNumber;
                    log.info("执行步数："+currentStep+"/"+maxSteps);

                    String stepResult=step();
                    if(isStuck())handStuckState();
                    String result="Step"+currentStep+":"+stepResult;
//                    results.add(result);
                    sseEmitter.send(result);
                }
                if(currentStep>=maxSteps){
                    state=AgentState.FINISHED;
                    sseEmitter.send("Terminated: Reached max step("+maxSteps+")");
//                    results.add("Terminated: Reached max step("+maxSteps+")");
                }
                sseEmitter.complete();;
            } catch (Exception e) {
                state=AgentState.ERROR;
                log.error("执行agent失败",e);
                try {
                    sseEmitter.send("执行agent失败"+e.getMessage());
                    sseEmitter.complete();

                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
            }finally {
                this.cleanUp();
            }
        });

        sseEmitter.onTimeout(()->{
            this.state=AgentState.ERROR;
            this.cleanUp();
            log.warn("SSE connection timed out");
        });

        sseEmitter.onCompletion(()->{
            if(this.state==AgentState.RUNNING){
                this.state=AgentState.FINISHED;
            }
            this.cleanUp();
            log.info("SSE connection completed");
        });
        return sseEmitter;


    }

   public abstract String step();

    /**
     * 判断是否出现死循环
     * @return
     */
    protected boolean isStuck(){
        List<Message> messageList = getMessageList();
        if(messageList==null||messageList.size()<2)
            return false;

        Message lastMessage = messageList.get(messageList.size() - 1);
        if(lastMessage.getText()==null||lastMessage.getText().isBlank())return false;

        long duplicateCount = messageList.subList(0, messageList.size() - 1)
                .stream()
                .filter(msg -> msg.getMessageType() == MessageType.ASSISTANT)
                .filter(msg -> Objects.equals(msg.getText(), lastMessage.getText()))
                .count();
        return duplicateCount>=getDuplicateThreshold();

    }

    /**
     * 处理死循环
     */
    protected void handStuckState() {
        String stuckPrompt = "Observed duplicate responses. Consider new strategies and avoid repeating ineffective paths already attempted.";
        this.nextStepPrompt = stuckPrompt + "\n" + this.nextStepPrompt;
    }
    protected void cleanUp(){

    }





}
