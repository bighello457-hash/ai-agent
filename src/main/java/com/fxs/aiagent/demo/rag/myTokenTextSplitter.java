package com.fxs.aiagent.demo.rag;


import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class myTokenTextSplitter {
    /**
     * 默认配置参数的TokenTextSplitter
     * @param documents
     * @return
     */
   public List<Document> splitDocument(List<Document>documents){
       TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
       return tokenTextSplitter.apply(documents);
   }

    /**
     * 自定义各个参数的TokenTextSplitter(从左到右分别为最大分块长度，最小分块长度，最小嵌入长度，最大分块个数，允许分隔符）
     * @param documents
     * @return
     */
   public List<Document> splitCustomized(List<Document>documents){
       TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(200, 100, 10, 5000, true);
       return tokenTextSplitter.apply(documents);
   }
}
