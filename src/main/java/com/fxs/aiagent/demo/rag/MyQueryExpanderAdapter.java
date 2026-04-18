/*
package com.fxs.aiagent.demo.rag;

import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.QueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * 将 MyMultiQueryExpander 适配为 Spring AI 的 QueryExpander 接口。
 * RetrievalAugmentationAdvisor 的 queryExpander() 方法要求传入 QueryExpander 接口的实现，
 * 而 MyMultiQueryExpander 返回的是 List<Query>，需要此适配器进行方法签名转换。
 *//*

@Component
public class MyQueryExpanderAdapter implements QueryExpander {
    private final MyMultiQueryExpander myMultiQueryExpander;

    public MyQueryExpanderAdapter(MyMultiQueryExpander myMultiQueryExpander) {
        this.myMultiQueryExpander = myMultiQueryExpander;
    }

    @Override
    public List<Query> expand(Query query) {
        return myMultiQueryExpander.getMultiQuery(query);
    }
}
*/
