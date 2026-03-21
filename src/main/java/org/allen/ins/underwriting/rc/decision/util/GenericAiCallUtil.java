package org.allen.ins.underwriting.rc.decision.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 静态AI接口调用工具类
 * 核心能力：
 * 1. 封装AI接口调用，仅暴露标准化调用接口
 * 2. 校验messages字段格式，避免请求异常
 * 3. 支持获取结构化响应/原始响应字符串两种模式
 */
@Component
@Slf4j
public class GenericAiCallUtil {

    // 静态成员变量
    private static String AI_BASE_URL;
    private static String AI_API_KEY;
    private static String AI_MODEL;
    private static int AI_TIMEOUT;
    private static OkHttpClient AI_CLIENT;

    // Spring注入的非静态变量
    @Value("${ai.general.base-url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String aiBaseUrl;

    @Value("${ai.general.api-key:290b7c50-1ecb-41c5-8d55-28a679a7d49b}")
    private String aiApiKey;

    @Value("${ai.doubao.model:doubao-seed-2-0-pro-260215}")
    private String aiModel;

    @Value("${ai.general.timeout:30}")
    private int aiTimeout;

    // 初始化静态变量
    @PostConstruct
    public void initStaticConfig() {
        AI_BASE_URL = this.aiBaseUrl;
        AI_API_KEY = this.aiApiKey;
        AI_MODEL = this.aiModel;
        AI_TIMEOUT = this.aiTimeout;

        if (AI_CLIENT == null) {
            AI_CLIENT = new OkHttpClient.Builder()
                    .connectTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }
        log.info("GenericAiCallUtil初始化完成，AI_BASE_URL:{}", AI_BASE_URL);
    }

    // ========== 标准化调用接口：返回结构化对象 ==========
    public static <T> T callAiApi(JSONObject requestBody, Class<T> responseType) throws IOException {
        // 入参校验
        Assert.notNull(requestBody, "请求体(requestBody)不能为空");
        Assert.notNull(responseType, "响应类型(responseType)不能为空");

        // 校验messages字段格式
        validateMessagesField(requestBody);

        // 自动填充模型名
        fillModelIfAbsent(requestBody);

        // 打印请求体日志
        String requestBodyStr = requestBody.toString();
        log.info("AI接口请求体（JSONObject入参）：{}", requestBodyStr);

        // 执行请求并解析响应
        String rawResponse = doPost(AI_BASE_URL, requestBodyStr);
        return parseAiResponse(rawResponse, responseType);
    }

    // ========== 标准化调用接口：字符串请求体 + 返回结构化对象 ==========
    public static <T> T callAiApi(String requestBodyStr, Class<T> responseType) throws IOException {
        // 入参校验
        Assert.notBlank(requestBodyStr, "请求体字符串(requestBodyStr)不能为空");
        Assert.notNull(responseType, "响应类型(responseType)不能为空");

        // 解析并校验messages字段
        JSONObject requestBody = JSONUtil.parseObj(requestBodyStr);
        validateMessagesField(requestBody);

        // 自动填充模型名
        fillModelIfAbsent(requestBody);
        String finalRequestStr = requestBody.toString();

        // 打印请求体日志
        log.info("AI接口请求体（String入参）：{}", finalRequestStr);

        // 执行请求并解析响应
        String rawResponse = doPost(AI_BASE_URL, finalRequestStr);
        return parseAiResponse(rawResponse, responseType);
    }

    // ========== 扩展接口：返回原始响应字符串（用于自定义解析） ==========
    public static String callAiApiForRawResponse(String requestBodyStr) throws IOException {
        // 入参校验
        Assert.notBlank(requestBodyStr, "请求体字符串(requestBodyStr)不能为空");

        // 解析并校验messages字段
        JSONObject requestBody = JSONUtil.parseObj(requestBodyStr);
        validateMessagesField(requestBody);

        // 自动填充模型名
        fillModelIfAbsent(requestBody);
        String finalRequestStr = requestBody.toString();

        // 打印请求体日志
        log.info("AI接口请求体（原始响应模式）：{}", finalRequestStr);

        // 执行请求并返回原始响应
        return doPost(AI_BASE_URL, finalRequestStr);
    }

    // ========== 扩展接口：自定义请求头 + 返回结构化对象 ==========
    public static <T> T callAiApi(JSONObject requestBody, Class<T> responseType, Headers customHeaders) throws IOException {
        Assert.notNull(customHeaders, "自定义请求头(customHeaders)不能为空");
        validateMessagesField(requestBody);

        // 自动填充模型名
        fillModelIfAbsent(requestBody);

        // 执行请求并解析响应
        String rawResponse = doPostWithCustomHeaders(AI_BASE_URL, requestBody.toString(), customHeaders);
        return parseAiResponse(rawResponse, responseType);
    }

    // ========== 私有工具方法：填充模型名 ==========
    private static void fillModelIfAbsent(JSONObject requestBody) {
        if (requestBody.getStr("model") == null || requestBody.getStr("model").isEmpty()) {
            requestBody.put("model", AI_MODEL);
            log.debug("自动填充model字段：{}", AI_MODEL);
        }
    }

    // ========== 私有工具方法：校验messages字段格式 ==========
    private static void validateMessagesField(JSONObject requestBody) {
        Object messagesObj = requestBody.get("messages");
        if (messagesObj == null) {
            throw new IllegalArgumentException("请求体缺少必填字段：messages");
        }
        if (!(messagesObj instanceof JSONArray)) {
            throw new IllegalArgumentException(
                    String.format("messages字段类型错误！预期JSON数组，实际类型：%s，值：%s",
                            messagesObj.getClass().getName(), messagesObj)
            );
        }
        JSONArray messagesArray = (JSONArray) messagesObj;
        if (messagesArray.isEmpty()) {
            throw new IllegalArgumentException("messages数组不能为空");
        }
        log.debug("messages字段校验通过，数组长度：{}", messagesArray.size());
    }

    // ========== 私有工具方法：执行POST请求 ==========
    private static String doPost(String fullUrl, String requestBodyStr) throws IOException {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"),
                requestBodyStr
        );

        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Bearer " + AI_API_KEY)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(requestBody)
                .build();

        try (Response response = AI_CLIENT.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "无响应内容";
                String errorMsg = String.format("AI接口调用失败，地址：%s，状态码：%d，响应：%s",
                        fullUrl, response.code(), errorBody);
                log.error(errorMsg);
                throw new IOException(errorMsg);
            }
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("AI接口原始响应：{}", responseBody);
            return responseBody;
        }
    }

    // ========== 私有工具方法：自定义请求头POST ==========
    private static String doPostWithCustomHeaders(String fullUrl, String requestBodyStr, Headers customHeaders) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUrl)
                .headers(customHeaders)
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBodyStr
                ));

        try (Response response = AI_CLIENT.newCall(requestBuilder.build()).execute()) {
            if (!response.isSuccessful()) {
                String errorMsg = "AI接口调用失败（自定义头），状态码：" + response.code();
                log.error(errorMsg);
                throw new IOException(errorMsg);
            }
            String responseBody = response.body() != null ? response.body().string() : "{}";
            log.info("AI接口原始响应（自定义头）：{}", responseBody);
            return responseBody;
        }
    }

    // ========== 私有工具方法：解析响应为指定类型 ==========
    private static <T> T parseAiResponse(String rawResponse, Class<T> responseType) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            rawResponse = "{}";
        }

        try {
            return JSONUtil.toBean(rawResponse, responseType);
        } catch (Exception e) {
            String errorMsg = String.format("AI响应解析失败，原始响应：%s，目标类型：%s",
                    rawResponse, responseType.getName());
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    // ========== 测试用：重置配置 ==========
    public static void resetConfig(String baseUrl, String apiKey, String model, int timeout) {
        AI_BASE_URL = baseUrl;
        AI_API_KEY = apiKey;
        AI_MODEL = model;
        AI_TIMEOUT = timeout;
        AI_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AI_TIMEOUT, TimeUnit.SECONDS)
                .build();
        log.info("GenericAiCallUtil配置已重置");
    }
}