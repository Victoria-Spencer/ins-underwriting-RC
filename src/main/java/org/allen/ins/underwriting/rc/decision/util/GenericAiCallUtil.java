package org.allen.ins.underwriting.rc.decision.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 静态AI接口调用工具类
 * 特性：
 * 1. 静态方法调用（GenericAiCallUtil.callAiApi(...)）
 * 2. 配置从Spring配置文件读取，初始化后赋值给静态变量
 * 3. OkHttp客户端静态单例，避免重复创建
 */
@Component // 保留@Component，让Spring初始化并注入配置
public class GenericAiCallUtil {
    // ========== 静态成员变量（核心改造点） ==========
    private static String AI_BASE_URL;
    private static String AI_API_KEY;
    private static String AI_MODEL;
    private static int AI_TIMEOUT;
    // 静态OkHttp客户端（单例）
    private static OkHttpClient AI_CLIENT;

    // ========== Spring注入的非静态变量（中转用） ==========
    @Value("${ai.general.base-url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String aiBaseUrl;

    @Value("${ai.general.api-key:290b7c50-1ecb-41c5-8d55-28a679a7d49b}")
    private String aiApiKey;

    @Value("${ai.doubao.model:doubao-seed-2-0-pro-260215}")
    private String aiModel;

    @Value("${ai.general.timeout:30}")
    private int aiTimeout;

    // ========== 初始化静态变量（核心：Spring初始化后赋值） ==========
    @PostConstruct
    public void initStaticConfig() {
        // 1. 把Spring注入的配置赋值给静态变量
        AI_BASE_URL = this.aiBaseUrl;
        AI_API_KEY = this.aiApiKey;
        AI_MODEL = this.aiModel;
        AI_TIMEOUT = this.aiTimeout;

        // 2. 初始化静态OkHttp客户端（单例）
        if (AI_CLIENT == null) {
            AI_CLIENT = new OkHttpClient.Builder()
                    .connectTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
    }

    /**
     * 静态通用AI接口POST调用方法
     * @param requestBody 请求体（JSON格式）
     * @param responseType 响应结果的目标类型
     * @param <T> 响应泛型类型
     * @return 解析后的目标类型对象
     * @throws IOException 接口调用异常
     */
    public static <T> T callAiApi(JSONObject requestBody, Class<T> responseType) throws IOException {
        // 1. 入参校验
        Assert.notNull(requestBody, "请求体(requestBody)不能为空");
        Assert.notNull(responseType, "响应类型(responseType)不能为空");

        // 2. 自动填充模型名到请求体
        if (requestBody.getStr("model") == null || requestBody.getStr("model").isEmpty()) {
            requestBody.put("model", AI_MODEL);
        }

        // 3. 执行POST请求
        String rawResponse = doPost(AI_BASE_URL, requestBody);

        // 4. 解析响应
        return parseAiResponse(rawResponse, responseType);
    }

    /**
     * 静态通用POST请求发送
     */
    private static String doPost(String fullUrl, JSONObject requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Bearer " + AI_API_KEY)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ))
                .build();

        try (Response response = AI_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMsg = String.format("AI接口调用失败，地址：%s，状态码：%d，响应：%s",
                        fullUrl, response.code(),
                        response.body() != null ? response.body().string() : "无响应内容");
                throw new IOException(errorMsg);
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }

    /**
     * 静态通用AI响应解析
     */
    private static <T> T parseAiResponse(String rawResponse, Class<T> responseType) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            rawResponse = "{}";
        }

        try {
            return JSONUtil.toBean(rawResponse, responseType);
        } catch (Exception e) {
            throw new RuntimeException("AI响应解析失败，原始响应：" + rawResponse + "，目标类型：" + responseType.getName(), e);
        }
    }

    // ========== 静态扩展方法：自定义请求头 ==========
    public static <T> T callAiApi(JSONObject requestBody, Class<T> responseType, Headers customHeaders) throws IOException {
        Assert.notNull(customHeaders, "自定义请求头(customHeaders)不能为空");

        if (requestBody.getStr("model") == null || requestBody.getStr("model").isEmpty()) {
            requestBody.put("model", AI_MODEL);
        }

        String rawResponse = doPostWithCustomHeaders(AI_BASE_URL, requestBody, customHeaders);
        return parseAiResponse(rawResponse, responseType);
    }

    private static String doPostWithCustomHeaders(String fullUrl, JSONObject requestBody, Headers customHeaders) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUrl)
                .headers(customHeaders)
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ));

        try (Response response = AI_CLIENT.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI接口调用失败（自定义头），状态码：" + response.code());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }

    // ========== 可选：重置静态配置（测试/动态调整用） ==========
    public static void resetConfig(String baseUrl, String apiKey, String model, int timeout) {
        AI_BASE_URL = baseUrl;
        AI_API_KEY = apiKey;
        AI_MODEL = model;
        AI_TIMEOUT = timeout;
        // 重新初始化OkHttp客户端
        AI_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}