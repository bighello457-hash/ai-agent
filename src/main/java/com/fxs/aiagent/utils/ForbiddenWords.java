package com.fxs.aiagent.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * 违禁词集合工具类
 * 包含政治、色情、暴力、违规营销等常见违禁词，可自行增删
 */
public class ForbiddenWords {

    // 违禁词集合（HashSet 查询速度 O(1)，适合过滤场景）
    private static final Set<String> FORBIDDEN_WORDS = new HashSet<>();

    static {
        // 初始化违禁词（可根据业务需求扩展）
        initForbiddenWords();
    }

    /**
     * 初始化所有违禁词
     */
    private static void initForbiddenWords() {
        // 政治敏感类
        FORBIDDEN_WORDS.add("习近平");
        FORBIDDEN_WORDS.add("习近平思想");
        FORBIDDEN_WORDS.add("习近平新时代中国特色社会主义思想");
        FORBIDDEN_WORDS.add("共产党");
        FORBIDDEN_WORDS.add("中国共产党");
        FORBIDDEN_WORDS.add("台独");
        FORBIDDEN_WORDS.add("港独");
        FORBIDDEN_WORDS.add("藏独");
        FORBIDDEN_WORDS.add("法轮功");
        FORBIDDEN_WORDS.add("六四");
        FORBIDDEN_WORDS.add("天安门");
        
        // 色情低俗类
        FORBIDDEN_WORDS.add("色情");
        FORBIDDEN_WORDS.add("淫秽");
        FORBIDDEN_WORDS.add("嫖娼");
        FORBIDDEN_WORDS.add("卖淫");
        FORBIDDEN_WORDS.add("约炮");
        FORBIDDEN_WORDS.add("裸聊");
        
        // 暴力违法类
        FORBIDDEN_WORDS.add("杀人");
        FORBIDDEN_WORDS.add("放火");
        FORBIDDEN_WORDS.add("爆炸");
        FORBIDDEN_WORDS.add("毒品");
        FORBIDDEN_WORDS.add("赌博");
        FORBIDDEN_WORDS.add("诈骗");
        FORBIDDEN_WORDS.add("走私");
        
        // 违规营销类
        FORBIDDEN_WORDS.add("办证");
        FORBIDDEN_WORDS.add("刻章");
        FORBIDDEN_WORDS.add("发票");
        FORBIDDEN_WORDS.add("迷药");
        FORBIDDEN_WORDS.add("枪支");
        FORBIDDEN_WORDS.add("弹药");
        
        // 辱骂攻击类
        FORBIDDEN_WORDS.add("傻逼");
        FORBIDDEN_WORDS.add("脑残");
        FORBIDDEN_WORDS.add("滚蛋");
        FORBIDDEN_WORDS.add("去死");
    }

    /**
     * 获取违禁词集合
     * @return 不可修改的违禁词集合
     */
    public static Set<String> getForbiddenWords() {
        return new HashSet<>(FORBIDDEN_WORDS);
    }

    /**
     * 判断字符串是否包含违禁词
     * @param content 待检测字符串
     * @return 包含返回true，不包含返回false
     */
    public static boolean containsForbiddenWord(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        for (String word : FORBIDDEN_WORDS) {
            if (content.contains(word)) {
                return true;
            }
        }
        return false;
    }

    // 测试示例
    public static void main(String[] args) {
        // 获取违禁词集合
        Set<String> words = ForbiddenWords.getForbiddenWords();
        System.out.println("违禁词总数：" + words.size());

        // 测试违禁词检测
        String test1 = "这个内容包含色情信息";
        String test2 = "今天天气很好";
        
        System.out.println("测试1是否包含违禁词：" + containsForbiddenWord(test1)); // true
        System.out.println("测试2是否包含违禁词：" + containsForbiddenWord(test2)); // false
    }
}