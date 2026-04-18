package com.fxs.aiagent.demo.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class createRAGAdvisorFactory {
    @Value("${spring.ai.dashscope.api-key}")
    private String API_KEY;
    @Resource
    private  ChatClient.Builder chatClientBuilder;

    @Resource
    private  VectorStore loveAppVectorStore;
    public  Advisor getRAGAdvisor(){
       /* Filter.Expression expression=new FilterExpressionBuilder()
                .eq("status","单身")
                        .build();*/

        VectorStoreDocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
                .topK(5)
//                .filterExpression(expression)
                .similarityThreshold(0.5)
                .vectorStore(loveAppVectorStore)
                .build();
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryExpander(new MyMultiQueryExpander(chatClientBuilder))
                .documentRetriever(retriever)
                .queryAugmenter(createRAGAugmentFactory.createInstance())
                .documentJoiner(new ConcatenationDocumentJoiner())
                .build();
        return retrievalAugmentationAdvisor;
    }
}
