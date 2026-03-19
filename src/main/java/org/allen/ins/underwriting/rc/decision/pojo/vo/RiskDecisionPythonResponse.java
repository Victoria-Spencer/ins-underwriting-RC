package org.allen.ins.underwriting.rc.decision.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 调用Python风控决策API的响应DTO
 * 说明：Python仅返回自身计算的风险结果（占70%权重），最终决策由Java端结合AI（30%）加权后生成
 * 字段和Python端RiskDecisionResponse严格对齐（camelCase自动转snake_case）
 */
@Data
public class RiskDecisionPythonResponse {
    /**
     * Python侧计算的风险概率（0-1，保留3位小数）
     * 约束说明：
     * 1. @Digits：整数位最多1位（0-1），小数位最多3位，入参时校验
     * 2. @JsonFormat：序列化返回前端时，强制保留3位小数（如0.8 → 0.800）
     */
    @Digits(integer = 1, fraction = 3, message = "Python风险概率仅支持0-1之间的数值，且最多保留3位小数")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.000")
    private BigDecimal pythonRiskProbability;

    /**
     * Python侧风险评估说明（可选，供Java端参考，如“健康风险占比60%，职业风险占比40%”）
     * 说明：非决策原因，仅为加权时提供参考维度
     */
    private String pythonRiskAnalysis;
}