package com.fxs.aiagent.demo.invoke;

import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;


public class VolcengineAiInvoke {

        public static void main(String[] args) {
            String apiKey = TestApiKey.VOAPI_KEY;
            // The base URL for model invocation
            ArkService arkService = ArkService.builder().apiKey(apiKey).baseUrl("https://ark.cn-beijing.volces.com/api/v3").build();

            CreateResponsesRequest request = CreateResponsesRequest.builder()
                    .model("doubao-seed-2-0-code-preview-260215")
                    .input(ResponsesInput.builder().stringValue("你好我是程序员").build()) // Replace with your prompt
                    // .thinking(ResponsesThinking.builder().type(ResponsesConstants.THINKING_TYPE_DISABLED).build()) //  Manually disable deep thinking
                    .build();

            ResponseObject resp = arkService.createResponse(request);
            System.out.println(resp);

            arkService.shutdownExecutor();
        }

}
