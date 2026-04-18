package com.fxs.aiagent.demo.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyMetaEnricher {
   /*//1:手动添加元信息 通过Map.of指定元信息(单个文档）
    Document document=new Document("我是恋爱大师，无论你提出什么问题，我都会为你解答", Map.of("type","单身","sex","男"));
    //2:利用DocumentReader批量添加元信息
    MarkdownDocumentReaderConfig config=MarkdownDocumentReaderConfig.builder()
            .withAdditionalMetadata("状态","单身").build();*/
    //3:自动添加元信息
    @Resource
    private ChatModel dashscopeChatModel;
    public List<Document> enrichDocuments(List<Document> documents){
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        List<Document> doc = enricher.apply(documents);
        return doc;
    }

    public List<Document> summaryEnrich(List<Document>documents){
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(dashscopeChatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        List<Document> apply = enricher.apply(documents);
        return apply;
    }

}
