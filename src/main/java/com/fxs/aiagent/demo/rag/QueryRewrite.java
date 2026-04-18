package com.fxs.aiagent.demo.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryRewrite {
    private final QueryTransformer queryTransformer;
  /*  public QueryRewrite(ChatModel dashscopeChatModel){
        ChatClient.Builder chatClientBuilder=ChatClient.builder(dashscopeChatModel);
        this.queryTransformer= RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }*/

    public QueryRewrite(ChatClient.Builder chatClientBuilder){

        this.queryTransformer= RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }
    public String  getRewrittenText(String message){
        return queryTransformer.transform(new Query(message)).text();
    }
}
