package com.fxs.aiagent.rag;

import com.fxs.aiagent.demo.rag.MyMetaEnricher;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fxs.aiagent.demo.rag.myTokenTextSplitter;

import java.util.List;

@Configuration
public class LoveAppVectorStoreConfig {
   /* @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        List<Document> documents = loveAppDocumentLoader.loadMarkdown();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }*/

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private myTokenTextSplitter myTokenTextSplitter;
    @Resource
    private MyMetaEnricher myMetaEnricher;
    @Bean
    VectorStore LoveAppVectorStore(EmbeddingModel embeddingModel){
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel)
                .build();
        List<Document> document = loveAppDocumentLoader.loadMarkdown();
        /*利用自定义文本分割器进行文本分割
        List<Document> documents = myTokenTextSplitter.splitCustomized(document);*/

        /*利用自定义关键词元信息增强器
        List<Document> documents = myMetaEnricher.enrichDocuments(document);*/

        /*自定义摘要元信息增强器
        List<Document> documents = myMetaEnricher.summaryEnrich(document);*/
        simpleVectorStore.add(document);
        return simpleVectorStore;
    }
}

