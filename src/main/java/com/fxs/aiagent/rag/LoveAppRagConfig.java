package com.fxs.aiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoveAppRagConfig {
    @Resource
    private VectorStore loveAppVectorStore;
    private MultiQueryExpander multiQueryExpander;
    @Bean
    public Advisor loveAppRag() {

        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(loveAppVectorStore)
                .topK(3)
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .queryExpander(multiQueryExpander)
                .documentRetriever(retriever)
                .documentJoiner(new ConcatenationDocumentJoiner())
                .build();
    }
}
