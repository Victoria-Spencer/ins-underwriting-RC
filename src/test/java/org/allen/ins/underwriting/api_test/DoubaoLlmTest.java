package org.allen.ins.underwriting.api_test;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * 纯 JUnit 测试：完全不依赖 Spring，不加载任何项目代码
 */
public class DoubaoLlmTest {

    // 硬编码配置（替代 @Value，彻底脱离 Spring）
    private static final String API_KEY = "290b7c50-1ecb-41c5-8d55-28a679a7d49b";
    private static final String BASE_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
    private static final String MODEL = "doubao-seed-2-0-pro-260215";

    // OkHttp客户端（不变）
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    // 测试方法（不变）
    @Test
    public void testDoubaoLlmCall() {
        String userPrompt = "2026年3月14日下午两点北京南到西安北买二等座";
        try {
            String response = callDoubaoLlm(userPrompt);
            System.out.println("LLM 原始响应：\n" + response);

            JSONObject responseJson = JSONUtil.parseObj(response);
            String ticketJson = responseJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");

            System.out.println("\n解析后的购票参数：\n" + ticketJson);

        } catch (IOException e) {
            System.err.println("请求失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 调用接口方法（不变）
    private String callDoubaoLlm(String userPrompt) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("temperature", 0.1);
        requestBody.put("stream", false);

        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "你是12306智能购票助手，仅解析购票指令为JSON，格式：{\"startStation\":\"\",\"endStation\":\"\",\"travelTime\":\"yyyy-MM-dd HH:mm\",\"seatType\":\"\"}");
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ))
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码：" + response.code() + "，响应：" + response.body().string());
            }
            return response.body().string();
        }
    }
}