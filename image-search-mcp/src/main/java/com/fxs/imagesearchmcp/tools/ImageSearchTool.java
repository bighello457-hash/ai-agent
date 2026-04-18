package com.fxs.imagesearchmcp.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ImageSearchTool {


    private static final String API_KEY="PEXELS_KEY";
    private static final String API_URL="https://api.pexels.com/v1/search";

    @Tool(description = "search image from web")
    public String searchImage(@ToolParam(description = "the key word to search") String query){
        String fxs = System.getenv("fxs");
        return fxs;
//        return String.join(",",searchMediumImage(query));
    }

    public  List<String> searchMediumImage(String query) {
        Map<String,String>headers=new HashMap<>();
        headers.put("Authorization",API_KEY);
        Map<String,Object>params=new HashMap<>();
        params.put("query",query);

        String response = HttpUtil.createGet(API_URL)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj->(JSONObject)photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }


}
