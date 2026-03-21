package org.allen.ins.underwriting.rc.decision.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.dao.dict.OccupationRiskDictMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.rc.decision.constant.RiskDecisionConstant;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.decision.dao.RiskDecisionMapper;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskAIAnalysisRequest;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionDTO;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionPythonRequest;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskAIAnalysisResponse;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionPythonResponse;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;
import org.allen.ins.underwriting.rc.decision.service.RiskDecisionService;
import org.allen.ins.underwriting.rc.decision.util.GenericAiCallUtil;
import org.allen.ins.underwriting.rc.decision.util.PythonApiHttpClient;
import org.allen.ins.underwriting.rc.factor.dao.RiskFactorMapper;
import org.allen.ins.underwriting.rc.factor.enums.DecisionResultEnum;
import org.allen.ins.underwriting.rc.factor.pojo.domain.RiskFactorRecord;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class RiskDecisionServiceImpl extends ServiceImpl<RiskDecisionMapper,RiskDecisionRecord>
        implements RiskDecisionService {

    @Resource
    private PythonApiHttpClient pythonApiHttpClient;
    @Resource
    private PolicyHolderMapper policyHolderMapper;
    @Resource
    private OccupationRiskDictMapper occupationRiskDictMapper;
    @Resource
    private RiskFactorMapper riskFactorMapper;

    // 定义5个等级的临界值（0-1区间，两位小数）
    private static final BigDecimal LEVEL_1_THRESHOLD = new BigDecimal("0.20");
    private static final BigDecimal LEVEL_2_THRESHOLD = new BigDecimal("0.40");
    private static final BigDecimal LEVEL_3_THRESHOLD = new BigDecimal("0.60");
    private static final BigDecimal LEVEL_4_THRESHOLD = new BigDecimal("0.80");
    private static final BigDecimal MAX_RISK_VALUE = new BigDecimal("1.00");
    private static final BigDecimal MIN_RISK_VALUE = new BigDecimal("0.00");
    // 百分比转换系数（0-1小数 → 0-100百分比）
    private static final BigDecimal PERCENT_SCALE = new BigDecimal("100");

    /**
     * 决策结果封装类（替代零散的finalDecision/decisionReason变量）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DecisionResult {
        private String finalDecision;       // 最终决策（承保/拒保）
        private String decisionReason;      // 决策原因
        private BigDecimal agentProb;       // AI复查后的风险概率（可选）
    }

    @Override
    public RiskDecisionVO calculate(RiskDecisionDTO request) {
        // ========== 1. 入参校验 & 基础数据获取 ==========
        validateRequest(request);
        RiskDecisionPythonResponse pythonResp = callPostRiskApi(request);

        // 校验python返回的风险概率非空 + 范围合法（0-1）
        BigDecimal pythonRiskProbability = validateAndGetPythonRiskProb(pythonResp);

        RiskDecisionRecord decisionRecord = initDecisionRecord(request, pythonRiskProbability);

        // ========== 2. 核心决策逻辑（主流程极简） ==========
        DecisionResult decisionResult = makeCoreDecision(request, pythonResp);

        // ========== 3. 决策记录赋值 & 保存（集中处理） ==========
        fillAndSaveDecisionRecord(decisionRecord, decisionResult, pythonRiskProbability);

        // ========== 4. 组装返回VO（单一出口） ==========
        return buildDecisionVO(decisionResult, pythonRiskProbability);
    }

    /**
     * 新增：校验并获取Python返回的风险概率（非空+0-1范围）
     */
    private BigDecimal validateAndGetPythonRiskProb(RiskDecisionPythonResponse pythonResp) {
        Assert.notNull(pythonResp, "Python风险评分接口返回为空");
        BigDecimal riskProb = pythonResp.getPythonRiskProbability();
        Assert.notNull(riskProb, "Python风险概率不能为空");
        Assert.isTrue(riskProb.compareTo(MIN_RISK_VALUE) >= 0 && riskProb.compareTo(MAX_RISK_VALUE) <= 0,
                "Python风险概率必须为0-1之间的数值");
        return riskProb;
    }

    /**
     * 组装返回VO（单一出口，逻辑清晰）
     */
    private RiskDecisionVO buildDecisionVO(DecisionResult decisionResult, BigDecimal pythonRiskProbability) {
        RiskDecisionVO vo = new RiskDecisionVO();
        vo.setDecisionResult(decisionResult.getFinalDecision());
        vo.setDecisionReason(decisionResult.getDecisionReason());
        vo.setRiskProb(decisionResult.getAgentProb() == null ? pythonRiskProbability : decisionResult.getAgentProb());
        return vo;
    }

    /**
     * 填充并保存决策记录（集中赋值，避免分支内零散操作）
     */
    private void fillAndSaveDecisionRecord(RiskDecisionRecord record, DecisionResult decisionResult, BigDecimal pythonRiskProbability) {
        // 基础字段赋值
        record.setRiskDecision(decisionResult.getFinalDecision());
        record.setDecisionReason(decisionResult.getDecisionReason());
        record.setDataModelProb(pythonRiskProbability);

        // AI复查分支的特殊字段赋值
        if (decisionResult.getAgentProb() != null) {
            record.setAgentProb(decisionResult.getAgentProb());
            record.setFinalRiskProb(decisionResult.getAgentProb());
        }

        // 兜底：确保finalRiskProb有值
        if (record.getFinalRiskProb() == null) {
            record.setFinalRiskProb(pythonRiskProbability);
        }

        // 统一保存
        this.save(record);
    }

    /**
     * 核心修正：BigDecimal比较逻辑 + 百分比格式化
     */
    private DecisionResult makeCoreDecision(RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        BigDecimal pythonRiskProbability = pythonResp.getPythonRiskProbability();

        // 1. 直接承保（0-0.3）：使用compareTo比较BigDecimal
        if (pythonRiskProbability.compareTo(RiskDecisionConstant.DIRECT_ACCEPT_THRESHOLD) <= 0) {
            return new DecisionResult(
                    DecisionResultEnum.DIRECT_ACCEPT.getChineseName(),
                    pythonResp.getDecisionConclusion(),
                    null
            );
        }

        // 2. 直接拒保（0.7-1）：使用compareTo比较BigDecimal
        if (pythonRiskProbability.compareTo(RiskDecisionConstant.DIRECT_REJECT_THRESHOLD) >= 0) {
            return new DecisionResult(
                    DecisionResultEnum.DIRECT_REJECT.getChineseName(),
                    pythonResp.getDecisionConclusion(),
                    null
            );
        }

        // 3. AI复查（0.3-0.7）：抽离到独立方法
        return handleAiReview(pythonRiskProbability, request, pythonResp);
    }

    /**
     * 修正：百分比格式化 + AI复查结果封装
     */
    private DecisionResult handleAiReview(BigDecimal pythonRiskProbability, RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        RiskAIAnalysisResponse aiResp = callRiskAIAnalysisApi(request, pythonResp);
        Assert.notNull(aiResp, "AI复查接口返回为空");
        Assert.notNull(aiResp.getFinalDecision(), "AI复查必须返回最终决策结果（承保/拒保）");
        Assert.notNull(aiResp.getReviewConclusion(), "AI格式化决策结论不能为空");

        // 修正：使用全参构造器（或lombok的@AllArgsConstructor）
        DecisionResult aiDecisionResult = new DecisionResult();
        BeanUtil.copyProperties(
                aiResp,
                aiDecisionResult,
                new CopyOptions().setFieldMapping(Map.of(
                        "reviewConclusion", "decisionReason",
                        "agentRiskProb", "agentProb"
                ))
        );

        return aiDecisionResult;
    }

    /**
     * 初始化决策记录（抽离重复的初始化逻辑）
     */
    private RiskDecisionRecord initDecisionRecord(RiskDecisionDTO request, BigDecimal pythonRiskProbability) {
        return new RiskDecisionRecord()
                .setPolicyHolderId(request.getPolicyHolderId())
                .setTraceId(TraceIdContext.getTraceId())
                .setAntiRecordId(1L);    // TODO 设置关联逆选择记录ID
    }

    /**
     * 入参全量校验（避免空指针）
     */
    private void validateRequest(RiskDecisionDTO request) {
        Assert.notNull(request, "风险决策请求参数不能为空");
        Assert.notNull(request.getPolicyHolderId(), "投保人ID不能为空");
        // 校验totalRiskValue范围（0-1）
        if (request.getTotalRiskValue() != null) {
            Assert.isTrue(request.getTotalRiskValue().compareTo(MIN_RISK_VALUE) >= 0
                            && request.getTotalRiskValue().compareTo(MAX_RISK_VALUE) <= 0,
                    "总风险值必须为0-1之间的数值");
        }
    }

    /**
     * 求体传错问题 + 投保人非空校验
     */
    RiskDecisionPythonResponse callPostRiskApi(RiskDecisionDTO request) {
        Long policyHolderId = request.getPolicyHolderId();
        // 投保人非空校验
        PolicyHolder policyHolder = policyHolderMapper.selectById(policyHolderId);
        Assert.notNull(policyHolder, "投保人信息不存在，policyHolderId=" + policyHolderId);

        Integer age = policyHolder.getAge();
        String occupation = policyHolder.getOccupation();
        BigDecimal occRiskValue = occupationRiskDictMapper.getRiskValueByOccupationName(occupation);
        int occRiskLevel = mapRiskValueToLevel(occRiskValue);

        int score = convertTwoDecimal01To0100Int(request.getTotalRiskValue());

        RiskDecisionPythonRequest riskDecisionPythonRequest = new RiskDecisionPythonRequest()
                .setTraceId(TraceIdContext.getTraceId())
                .setTotalRiskScore(score)
                .setOccupationRiskLevel(occRiskLevel)
                .setAge(age)
                .setInsureAmount(request.getInsureAmount())
                .setHasHistoryDisease(false);

        // 请求体 riskDecisionPythonRequest
        return pythonApiHttpClient.callPythonApi(
                HttpMethod.POST,
                "/risk/calculate",
                riskDecisionPythonRequest,
                RiskDecisionPythonResponse.class
        );
    }

    /**
     * 0-1小数转0-100整数
     */
    public int convertTwoDecimal01To0100Int(BigDecimal totalRiskValue) {
        if (totalRiskValue == null) {
            return 0;
        }
        BigDecimal multiplied = totalRiskValue.multiply(PERCENT_SCALE);
        int intValue;
        try {
            intValue = multiplied.intValueExact();
        } catch (ArithmeticException e) {
            intValue = multiplied.setScale(0, RoundingMode.HALF_UP).intValue();
        }
        return Math.max(0, Math.min(100, intValue));
    }

    /**
     * 风险值映射等级（逻辑不变，保留）
     */
    public int mapRiskValueToLevel(BigDecimal riskValue) {
        if (riskValue == null) {
            return 0;
        }
        BigDecimal normalizedValue = riskValue.max(MIN_RISK_VALUE).min(MAX_RISK_VALUE)
                .setScale(2, RoundingMode.HALF_UP);
        if (normalizedValue.compareTo(LEVEL_1_THRESHOLD) < 0) {
            return 0;
        } else if (normalizedValue.compareTo(LEVEL_2_THRESHOLD) < 0) {
            return 1;
        } else if (normalizedValue.compareTo(LEVEL_3_THRESHOLD) < 0) {
            return 2;
        } else if (normalizedValue.compareTo(LEVEL_4_THRESHOLD) < 0) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * 新增：AI复查接口调用实现
     */
    private RiskAIAnalysisResponse callRiskAIAnalysisApi(RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        // AI复查
        JSONObject requestBody = getRequestBody(request, pythonResp);


        RiskAIAnalysisResponse aiResp = null;

        try {
            aiResp = GenericAiCallUtil.callAiApi(requestBody, RiskAIAnalysisResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BigDecimal agentRiskProb = aiResp.getAgentRiskProb();

        // AI复查决策逻辑示例：0.3-0.5承保，0.5-0.7拒保
        if (agentRiskProb.compareTo(new BigDecimal("0.5")) <= 0) {
            aiResp.setFinalDecision(DecisionResultEnum.DIRECT_ACCEPT.getChineseName());
//            aiResp.setReviewConclusion("AI复查通过，风险可控，建议承保");
        } else {
            aiResp.setFinalDecision(DecisionResultEnum.DIRECT_REJECT.getChineseName());
//            aiResp.setReviewConclusion("AI复查发现潜在风险，建议拒保");
        }
        return aiResp;
    }

    private JSONObject getRequestBody(RiskDecisionDTO request, RiskDecisionPythonResponse pythonResp) {
        String traceId = TraceIdContext.getTraceId();
        RiskFactorRecord factorRecord = riskFactorMapper.selectOne(
                new QueryWrapper<RiskFactorRecord>().eq("trace_id", traceId)
        );

        if (factorRecord == null) {
            throw new RuntimeException("未查询到traceId=" + traceId + "对应的风险因子记录");
        }

        BigDecimal ageRiskValue = factorRecord.getAgeRiskValue();
        BigDecimal occupationRiskValue = factorRecord.getOccupationRiskValue();
        BigDecimal amountRiskValue = factorRecord.getAmountRiskValue();
        BigDecimal healthRiskValue = factorRecord.getHealthRiskValue();
        BigDecimal totalRiskValue = factorRecord.getTotalRiskValue();

        // ========== 核心调整：和Python端对齐的AI指令话术 ==========
        String aiPrompt = String.format(
                "请严格按照以下要求复核投保人%s的风险信息：\n" +
                        "1. 基础风险信息：\n" +
                        "   - Python计算风险概率：%s\n" +
                        "   - Python风险分析：%s\n" +
                        "   - 年龄风险值：%s\n" +
                        "   - 职业风险值：%s\n" +
                        "   - 保额风险值：%s\n" +
                        "   - 健康风险值：%s\n" +
                        "   - 总风险值：%s\n" +
                        "2. 返回要求（必须严格遵守，否则无法解析，无额外冗余文字）：\n" +
                        "   - 风险概率（agentRiskProb）：仅返回0-1之间的纯小数，保留6位小数（示例：0.450123），无单位、无解释性文字；\n" +
                        "   - 风险评估说明（riskAnalysis）：返回详细的风险复核理由，围绕投保人风险特征展开，字数控制在50-200字，逻辑清晰、客观中立；\n" +
                        "   - 复查结论（reviewConclusion）：格式为「关键原因，次要原因，风险概率（保留6位小数），最终决策」，用中文逗号分隔，最终决策仅填「承保」或「拒保」，示例：风险因子总分85（极高），存在既往病史，0.850，拒保，无任何额外文字、标点或格式；\n" +
                        "   - 最终决策（finalDecision）：仅返回「承保」或「拒保」其中一个纯文字值，无任何额外字符；",
                request.getPolicyHolderId(),
                formatBigDecimal(pythonResp.getPythonRiskProbability()),
                pythonResp.getPythonRiskAnalysis() == null ? "无" : pythonResp.getPythonRiskAnalysis(),
                formatBigDecimal(ageRiskValue),
                formatBigDecimal(occupationRiskValue),
                formatBigDecimal(amountRiskValue),
                formatBigDecimal(healthRiskValue),
                formatBigDecimal(totalRiskValue)
        );

        // ========== 和Python端对齐的JSON请求体 ==========
        return new JSONObject()
                // 1. 核心字段：messages（Python端约定的字段名）
                .put("messages", new JSONArray().add(new JSONObject()
                        .put("role", "user")  // Python端通常约定role为user/assistant
                        .put("content", aiPrompt)  // 结构化指令，确保返回结果可解析
                ))
                // 2. 温度参数：和Python端约定的取值范围一致（0-1）
                .put("temperature", 0.1)
                // 3. 流式返回：Python端通常false表示一次性返回
                .put("stream", false);
    }

    private String formatBigDecimal(BigDecimal value) {
        return value == null ? "0.000000" : value.setScale(6, RoundingMode.HALF_UP).toString();
    }
}