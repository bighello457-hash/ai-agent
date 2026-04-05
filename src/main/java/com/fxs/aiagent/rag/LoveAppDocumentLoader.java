package com.fxs.aiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LoveAppDocumentLoader {
  /*  private final ResourcePatternResolver resourcePatternResolver;
    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver=resourcePatternResolver;
    }

    *//**
     * 加载所有的文档
     * @return
     *//*
    List<Document> loadMarkdown(){
        List<Document> allDocuments=new ArrayList<>();
        try {
            //1利用resourcePatterResolver进行读取指定目录下的文件资源
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                //2构建MarkdownDocumentReader配置文件
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", filename)
                        .build();
                //3利用MarkdownDocumentReader进行加载资源
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                //4将加载的资源添加进文档集合
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("markdown文档加载失败",e);
        }
        return allDocuments;
    }

    */


    private ResourcePatternResolver resourcePatternResolver;
    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver=resourcePatternResolver;
    }
    List<Document> getDocument(){
        List<Document> result=new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:/document/恋爱对象档案.md");

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeBlockquote(false)
                        .withIncludeCodeBlock(false)
                        .withAdditionalMetadata("filename", filename)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                result.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("文档加载失败");
        }
        return result;
    }


}
