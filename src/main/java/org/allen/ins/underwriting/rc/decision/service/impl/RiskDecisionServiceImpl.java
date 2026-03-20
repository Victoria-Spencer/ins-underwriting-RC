package org.allen.ins.underwriting.rc.decision.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.allen.ins.underwriting.common.util.TraceIdContext;
import org.allen.ins.underwriting.dao.PolicyHolderMapper;
import org.allen.ins.underwriting.dao.dict.OccupationRiskDictMapper;
import org.allen.ins.underwriting.pojo.domain.PolicyHolder;
import org.allen.ins.underwriting.rc.decision.pojo.domain.RiskDecisionRecord;
import org.allen.ins.underwriting.rc.decision.dao.RiskDecisionMapper;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionDTO;
import org.allen.ins.underwriting.rc.decision.pojo.dto.RiskDecisionPythonRequest;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionPythonResponse;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;
import org.allen.ins.underwriting.rc.decision.service.RiskDecisionService;
import org.allen.ins.underwriting.rc.decision.util.PythonApiHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RiskDecisionServiceImpl extends ServiceImpl<RiskDecisionMapper,RiskDecisionRecord>
        implements RiskDecisionService {

    @Resource
    private PythonApiHttpClient pythonApiHttpClient;
    @Resource
    private PolicyHolderMapper policyHolderMapper;
    @Resource
    private OccupationRiskDictMapper occupationRiskDictMapper;

    // 定义5个等级的临界值（0-1区间，两位小数）
    private static final BigDecimal LEVEL_1_THRESHOLD = new BigDecimal("0.20");
    private static final BigDecimal LEVEL_2_THRESHOLD = new BigDecimal("0.40");
    private static final BigDecimal LEVEL_3_THRESHOLD = new BigDecimal("0.60");
    private static final BigDecimal LEVEL_4_THRESHOLD = new BigDecimal("0.80");
    private static final BigDecimal MAX_RISK_VALUE = new BigDecimal("1.00");
    private static final BigDecimal MIN_RISK_VALUE = new BigDecimal("0.00");


    @Override
    public RiskDecisionVO calculate(RiskDecisionDTO request) {
        // 1. 调用Python接口
        RiskDecisionPythonResponse pythonResp = callPostRiskApi(request);
        // 2. 调用AI接口
        RiskAIAnalysisResponse aiResp = callRiskAIAnalysisApi(request, pythonResp);
        // 3. 加权融合+原因整合
        return mergePythonAndAI(pythonResp, aiResp);
    }

    RiskDecisionPythonResponse callPostRiskApi(RiskDecisionDTO request) {
        Long policyHolderId = request.getPolicyHolderId();
        PolicyHolder policyHolder = policyHolderMapper.selectById(policyHolderId);
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

        return pythonApiHttpClient.callPythonApi(
                HttpMethod.POST,       // 手动指定POST请求
                "/risk/calculate",     // 路径参数
                request,               // 请求体
                RiskDecisionPythonResponse.class // 响应类型
        );
    }

    /**
     * 将0-1之间、仅两位小数的BigDecimal转为0-100的整数
     * @param totalRiskValue 0-1之间的两位小数（可能为null）
     * @return 0-100的整数
     */
    public int convertTwoDecimal01To0100Int(BigDecimal totalRiskValue) {
        // 空值处理：null直接返回0
        if (totalRiskValue == null) {
            return 0;
        }

        // 核心操作：两位小数的0-1数值 × 100（必然得到整数）
        BigDecimal multiplied = totalRiskValue.multiply(new BigDecimal("100"));

        // 转为整数（因是两位小数，无需四舍五入，直接取整即可）
        int intValue;
        try {
            // intValueExact：确保是整数（若输入非两位小数，抛异常，也可根据业务调整）
            intValue = multiplied.intValueExact();
        } catch (ArithmeticException e) {
            // 极端情况（如输入非两位小数）：兜底用常规取整
            intValue = multiplied.setScale(0, RoundingMode.HALF_UP).intValue();
        }

        // 范围兜底：确保结果严格在0-100之间
        return Math.max(0, Math.min(100, intValue));
    }

    /**
     * 将0-1区间的riskValue映射为0-4的整数等级（5个等级）
     * @param riskValue 0-1之间的两位小数（允许null，允许超出0-1范围）
     * @return 0-4的整数（0=低风险，1=较低风险，2=中风险，3=高风险，4=极高风险）
     */
    public int mapRiskValueToLevel(BigDecimal riskValue) {
        // 1. 空值处理：null直接返回0（低风险）
        if (riskValue == null) {
            return 0;
        }

        // 2. 范围兜底：确保数值在0-1之间（负数→0，大于1→1）
        BigDecimal normalizedValue = riskValue.max(MIN_RISK_VALUE).min(MAX_RISK_VALUE);
        // 确保数值是两位小数（避免0.199999这类精度问题导致判断错误）
        normalizedValue = normalizedValue.setScale(2, RoundingMode.HALF_UP);

        // 3. 区间判断，映射到0-4的等级
        if (normalizedValue.compareTo(LEVEL_1_THRESHOLD) < 0) {
            return 0; // [0.00, 0.20) → 0级
        } else if (normalizedValue.compareTo(LEVEL_2_THRESHOLD) < 0) {
            return 1; // [0.20, 0.40) → 1级
        } else if (normalizedValue.compareTo(LEVEL_3_THRESHOLD) < 0) {
            return 2; // [0.40, 0.60) → 2级
        } else if (normalizedValue.compareTo(LEVEL_4_THRESHOLD) < 0) {
            return 3; // [0.60, 0.80) → 3级
        } else {
            return 4; // [0.80, 1.00] → 4级
        }
    }
}
