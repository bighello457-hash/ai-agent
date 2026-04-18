package com.fxs.aiagent.demo.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyMultiQueryExpander implements QueryExpander {
   private final MultiQueryExpander multiQueryExpander;
   public MyMultiQueryExpander(ChatClient.Builder chatClientBuilder){
       this.multiQueryExpander=MultiQueryExpander.builder()
               .chatClientBuilder(chatClientBuilder)
               .numberOfQueries(3)
               .includeOriginal(true)
               .build();
   }
//   public List<Query> getMultiQuery(Query query){
//       return multiQueryExpander.expand(query);
//   }

    @Override
    public List<Query> expand(Query query) {
       return multiQueryExpander.apply(query);
    }
}
