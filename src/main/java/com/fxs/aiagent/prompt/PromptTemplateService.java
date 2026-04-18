package com.fxs.aiagent.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prompt模板加载与渲染服务
 * 支持从资源文件加载模板，并使用变量替换生成最终prompt
 *
 * @author fxs
 */
@Slf4j
@Service
public class PromptTemplateService {

    private final ResourceLoader resourceLoader;
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    private static final String TEMPLATE_PREFIX = "classpath:prompts/";

    public PromptTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 加载模板并渲染
     *
     * @param templateName 模板文件名（不含路径，如 "football-analysis.st"）
     * @param variables    变量映射
     * @return 渲染后的prompt
     */
    public String render(String templateName, Map<String, Object> variables) {
        String template = loadTemplate(templateName);
        return renderTemplate(template, variables);
    }

    /**
     * 加载模板（带缓存）
     *
     * @param templateName 模板文件名
     * @return 模板内容
     */
    public String loadTemplate(String templateName) {
        return templateCache.computeIfAbsent(templateName, name -> {
            try {
                Resource resource = resourceLoader.getResource(TEMPLATE_PREFIX + name);
                if (!resource.exists()) {
                    throw new IllegalArgumentException("模板文件不存在: " + name);
                }
                String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                log.info("成功加载模板: {}", name);
                return content;
            } catch (IOException e) {
                log.error("加载模板失败: {}", name, e);
                throw new RuntimeException("加载模板失败: " + name, e);
            }
        });
    }

    /**
     * 重新加载模板（清除缓存）
     *
     * @param templateName 模板文件名
     * @return 模板内容
     */
    public String reloadTemplate(String templateName) {
        templateCache.remove(templateName);
        return loadTemplate(templateName);
    }

    /**
     * 清除所有缓存
     */
    public void clearCache() {
        templateCache.clear();
        log.info("模板缓存已清除");
    }

    /**
     * 渲染模板（变量替换）
     * 支持格式：{variableName}
     *
     * @param template 模板内容
     * @param variables 变量映射
     * @return 渲染后的内容
     */
    private String renderTemplate(String template, Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return template;
        }

        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
