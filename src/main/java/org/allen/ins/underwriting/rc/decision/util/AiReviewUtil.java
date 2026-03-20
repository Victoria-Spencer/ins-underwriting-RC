package org.allen.ins.underwriting.rc.decision.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.*;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionDTO;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskAIAnalysisResponse;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionPythonResponse;
import org.allen.ins.underwriting.rc.decision.service.impl.RiskDecisionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * AI复查接口调用工具类
 * 职责：封装AI复查的接口调用、参数组装、响应解析
 */
@Component
public class AiReviewUtil {
    // ========== AI接口配置（建议配置在yml，这里先用硬编码测试） ==========
    @Value("${ai.review.base-url:https://your-ai-api.com/v1/review}") // 实际替换为你的AI接口地址
    private String aiReviewBaseUrl;

    @Value("${ai.review.api-key:your-ai-api-key}") // AI接口密钥
    private String aiReviewApiKey;

    @Value("${ai.review.timeout:30}") // 超时时间（秒）
    private int aiReviewTimeout;

    // OkHttp客户端（单例复用）
    private final OkHttpClient aiClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 调用AI复查接口
     * @param request 风控请求参数
     * @param pythonResp Python返回的风险数据
     * @return AI复查响应结果
     */
    public RiskAIAnalysisResponse callAiReview(RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        // 1. 参数校验
        Assert.notNull(request, "AI复查请求参数不能为空");
        Assert.notNull(pythonResp, "Python风险数据不能为空");
        Assert.notNull(pythonResp.getPythonRiskProbability(), "Python风险概率不能为空");

        // 2. 组装AI接口请求参数
        JSONObject requestBody = buildAiReviewRequest(request, pythonResp);

        // 3. 调用AI接口
        String aiResponse = doPost(aiReviewBaseUrl, requestBody);

        // 4. 解析AI响应为业务对象
        return parseAiReviewResponse(aiResponse, pythonResp.getPythonRiskProbability());
    }

    /**
     * 组装AI复查接口的请求参数
     */
    private JSONObject buildAiReviewRequest(RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        JSONObject requestBody = new JSONObject();
        // 必传参数：投保人ID、Python风险概率等
        requestBody.put("policyHolderId", request.getPolicyHolderId());
        requestBody.put("pythonRiskProbability", pythonResp.getPythonRiskProbability().toString());
        requestBody.put("pythonRiskAnalysis", pythonResp.getPythonRiskAnalysis());

        return requestBody;
    }

    /**
     * 发送POST请求调用AI接口
     */
    private String doPost(String url, JSONObject requestBody) {
        // 构建请求头
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + aiReviewApiKey)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .post(RequestBody.create(
                        MediaType.parse("application/json;charset=UTF-8"),
                        requestBody.toString()
                ))
                .build();

        // 执行请求并处理响应
        try (Response response = aiClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI复查接口调用失败，状态码：" + response.code() + "，响应：" + (response.body() != null ? response.body().string() : "无响应"));
            }
            return response.body() != null ? response.body().string() : "";
        } catch (IOException e) {
            throw new RuntimeException("AI复查接口调用异常：" + e.getMessage(), e);
        }
    }

    /**
     * 解析AI接口响应为业务对象（适配你定义的RiskAIAnalysisResponse）
     */
    private RiskAIAnalysisResponse parseAiReviewResponse(String aiResponse, BigDecimal pythonRiskProb) {
        // 1. 解析JSON响应
        JSONObject responseJson = JSONUtil.parseObj(aiResponse);

        // 2. 封装为业务对象（字段名需和AI接口返回对齐，这里是示例）
        RiskAIAnalysisResponse aiResp = new RiskAIAnalysisResponse();
        aiResp.setFinalDecision(responseJson.getStr("finalDecision")); // AI返回的最终决策（承保/拒保）
        aiResp.setReviewConclusion(responseJson.getStr("reviewConclusion")); // AI复查结论
        // 解析AI返回的风险概率（如果AI返回，优先用AI的；否则用模拟逻辑）
        String agentProbStr = responseJson.getStr("agentRiskProb");
        if (agentProbStr != null && !agentProbStr.isEmpty()) {
            aiResp.setAgentRiskProb(new BigDecimal(agentProbStr).setScale(3, RoundingMode.HALF_UP));
        } else {
            // 兼容模拟逻辑：AI接口未返回时，用原有的小数运算逻辑
            if (pythonRiskProb.compareTo(new BigDecimal("0.5")) <= 0) {
                aiResp.setAgentRiskProb(pythonRiskProb.multiply(new BigDecimal("0.9")).setScale(3, RoundingMode.HALF_UP));
            } else {
                aiResp.setAgentRiskProb(pythonRiskProb.multiply(new BigDecimal("1.1")).setScale(3, RoundingMode.HALF_UP));
            }
        }
        aiResp.setAiReviewData(responseJson); // 保存原始AI响应，用于溯源

        // 3. 校验核心字段
        Assert.notNull(aiResp.getFinalDecision(), "AI复查响应中finalDecision不能为空");
        return aiResp;
    }
}