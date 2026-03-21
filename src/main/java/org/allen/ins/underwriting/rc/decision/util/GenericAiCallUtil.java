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
 * 通用AI接口调用工具类
 * 职责：封装通用的AI接口POST调用、参数传递、JSON响应解析（支持任意响应类型）
 * 适用场景：各类AI接口调用，无需绑定具体业务（如风险复查、智能核保、文本分析等）
 */
@Component
public class GenericAiCallUtil {
    // ========== AI接口通用配置（从配置文件读取，支持多环境） ==========
    @Value("${ai.general.base-url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}") // 通用AI接口基础地址
    private String aiBaseUrl;

    @Value("${ai.general.api-key:290b7c50-1ecb-41c5-8d55-28a679a7d49b}") // AI接口通用密钥（不同接口可单独覆盖）
    private String aiApiKey;

    @Value("${ai.doubao.model:doubao-seed-2-0-pro-260215}") // 模型名配置
    private String aiModel;

    @Value("${ai.general.timeout:30}") // 通用超时时间（秒）
    private int aiTimeout;

    // OkHttp客户端（单例复用，通用配置）
    private final OkHttpClient aiClient;

    // 构造器初始化OkHttp客户端（支持配置超时时间）
    public GenericAiCallUtil() {
        // 临时兜底：如果配置未注入，默认30秒超时
        int timeout = 30;
        this.aiClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    @PostConstruct
    public void initOkHttpClient() {
        this.aiClient.newBuilder()
                .connectTimeout(aiTimeout, TimeUnit.SECONDS)
                .readTimeout(aiTimeout, TimeUnit.SECONDS)
                .writeTimeout(aiTimeout, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 通用AI接口POST调用方法（核心泛型方法）
     * @param requestBody 请求体（JSON格式）
     * @param responseType 响应结果的目标类型（如 RiskAIAnalysisResponse.class）
     * @param <T> 响应泛型类型
     * @return 解析后的目标类型对象
     * @throws IOException 接口调用异常
     */
    public <T> T callAiApi(JSONObject requestBody, Class<T> responseType) throws IOException {
        // 1. 入参校验（通用校验，避免空指针）
        Assert.notNull(requestBody, "请求体(requestBody)不能为空");
        Assert.notNull(responseType, "响应类型(responseType)不能为空");

        // 2. 自动填充模型名到请求体（如果请求体未传model，用配置文件的值）
        if (requestBody.getStr("model") == null || requestBody.getStr("model").isEmpty()) {
            requestBody.put("model", aiModel);
        }

        // 3. 执行POST请求，获取原始JSON响应
        String rawResponse = doPost(aiBaseUrl, requestBody);

        // 4. 通用JSON解析（支持任意目标类型）
        return parseAiResponse(rawResponse, responseType);
    }

    /**
     * 通用POST请求发送（底层HTTP调用，和业务解耦）
     */
    private String doPost(String fullUrl, JSONObject requestBody) throws IOException {
        // 构建HTTP请求（通用请求头，可根据接口要求扩展）
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Bearer " + aiApiKey) // 你的API_KEY
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ))
                .build();

        // 执行请求并处理响应（try-with-resources自动关闭响应体）
        try (Response response = aiClient.newCall(request).execute()) {
            // 非2xx状态码抛异常，上层统一处理
            if (!response.isSuccessful()) {
                String errorMsg = String.format("AI接口调用失败，地址：%s，状态码：%d，响应：%s",
                        fullUrl, response.code(),
                        response.body() != null ? response.body().string() : "无响应内容");
                throw new IOException(errorMsg);
            }

            // 返回响应体字符串（空值兜底）
            return response.body() != null ? response.body().string() : "{}";
        }
    }

    /**
     * 通用AI响应解析（支持任意泛型类型，解耦业务解析逻辑）
     * @param rawResponse AI接口原始JSON响应
     * @param responseType 目标解析类型
     * @param <T> 泛型类型
     * @return 解析后的目标对象
     */
    private <T> T parseAiResponse(String rawResponse, Class<T> responseType) {
        // 空响应兜底
        if (rawResponse == null || rawResponse.isEmpty()) {
            rawResponse = "{}";
        }

        // 通用JSON转对象（Hutool工具类支持任意POJO解析）
        try {
            return JSONUtil.toBean(rawResponse, responseType);
        } catch (Exception e) {
            throw new RuntimeException("AI响应解析失败，原始响应：" + rawResponse + "，目标类型：" + responseType.getName(), e);
        }
    }

    // ========== 可选扩展方法（按需添加） ==========
    /**
     * 重载方法：支持自定义请求头（如不同接口需要不同的API Key）
     */
    public <T> T callAiApi(JSONObject requestBody, Class<T> responseType, Headers customHeaders) throws IOException {
        Assert.notNull(customHeaders, "自定义请求头(customHeaders)不能为空");

        // 自动填充模型名到请求体
        if (requestBody.getStr("model") == null || requestBody.getStr("model").isEmpty()) {
            requestBody.put("model", aiModel);
        }

        String rawResponse = doPostWithCustomHeaders(aiBaseUrl, requestBody, customHeaders);
        return parseAiResponse(rawResponse, responseType);
    }

    /**
     * 带自定义请求头的POST请求
     */
    private String doPostWithCustomHeaders(String fullUrl, JSONObject requestBody, Headers customHeaders) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUrl)
                .headers(customHeaders)
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ));

        try (Response response = aiClient.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI接口调用失败（自定义头），状态码：" + response.code());
            }
            return response.body() != null ? response.body().string() : "{}";
        }
    }
}