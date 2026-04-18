package com.fxs.aiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent{
    /**
     * 判断是否需要执行行动
     * true 需要，false 不需要
     * @return
     */
    public abstract boolean think();
    public abstract String act();

    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if(!shouldAct){
                return "思考完成，无需行动";
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "步骤执行失败："+e.getMessage();
        }
    }
}
