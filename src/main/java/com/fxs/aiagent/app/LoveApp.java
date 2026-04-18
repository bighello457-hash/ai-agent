package com.fxs.aiagent.app;

import com.fxs.aiagent.advisor.ProfanityFilterAdvisor;
import com.fxs.aiagent.advisor.TimeAdvisor;
import com.fxs.aiagent.chatMemory.FileBasedChatMemory;
import com.fxs.aiagent.demo.rag.QueryRewrite;
import com.fxs.aiagent.demo.rag.createRAGAugmentFactory;
import com.fxs.aiagent.tools.FileOperationTool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import com.fxs.aiagent.demo.rag.createRAGAdvisorFactory;
import com.fxs.aiagent.demo.rag.createRAGAugmentFactory;
import reactor.core.publisher.Flux;


import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class LoveApp {
//    @Resource
//    public ToolCallback[] allTools;
    String fileDir = System.getProperty("user.dir") + "/chat-memory";
    ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
    private ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatClient.Builder builder) {
        chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // new MessageChatAdvisor(chatMemory),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
//                        new ProfanityFilterAdvisor(),
                        // new myLogAdvisor(),
//                        new TimeAdvisor()
                // new Re2Advisor()
                )

                .build();
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String text = response.getResult().getOutput().getText();
        return text;
    }

    /**
     * 支持流式调用
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
        //之所以不返回chatResponse是因为这里面包含了太多的原信息

    }

    /**
     * 生成恋爱报告
     * 
     * @param title
     * @param suggestions
     */
    record LoveReport(String title, List<String> suggestions) {
    };

    public LoveReport doChatWithReport(String message, String chatId) {

        LoveReport result = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport:{}", result);
        return result;
    }


    /*@Resource
    private VectorStore loveAppVectorStore;
    public String doChatWithVectorStore(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}",content);
        return content;
    }*/

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private Advisor loveAppRagCloudAdvisorWithMultiQuery;
    @Resource
    private Advisor loveAppRag;
    @Resource
    private QueryRewrite queryRewrite;
    @Resource
    private createRAGAdvisorFactory createRAGAdvisorFactory;

    public String doChatWithRag(String message, String chatId) {
        Advisor ragAdvisor = createRAGAdvisorFactory.getRAGAdvisor();
        /*自定义查询重写器进行查询重写
        String rewrittenText = queryRewrite.getRewrittenText(message);*/
//        log.info("doChatWithRag 开始，使用 loveAppRagCloudAdvisorWithMultiQuery, Advisor类型={}", loveAppRagCloudAdvisorWithMultiQuery.getClass().getName());
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(ragAdvisor)
//                .advisors(QuestionAnswerAdvisor.builder(LoveAppVectorStore).build())

                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
//        log.info("doChatWithRag 完成，content={}", content);
        return content;
    }

    @Resource
    private VectorStore LoveAppVectorStore;

   /* public String doChatWithAppVectorStore(String message,String chatId){
        String content = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(QuestionAnswerAdvisor.builder(LoveAppVectorStore).build())

                .call()
                .chatResponse()
                .getResult().getOutput().getText();
        return content;
    }
*/


    public String  doChatWithTools(String message,String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
//                .tools(allTools)
//                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        return content;
    }


    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ToolCallbackProvider mcpToolCallbacks;


    public String doChatWithMcp(String message, String chatId) {

        String content = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .toolCallbacks(mcpToolCallbacks)
                .toolCallbacks(Arrays.asList(allTools))
                .call()
                .chatResponse().getResult().getOutput().getText();
        return content;
    }



}
