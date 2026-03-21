package org.allen.ins.underwriting.rc.decision.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class RiskAIAnalysisRequest {

    @NotNull(message = "投保人ID不能为空")
    private Long PolicyHolderId;

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
     * Python侧风险评估说明
     */
    private String pythonRiskAnalysis;

    // 风险因子

    /**
     * 年龄风险因子总分（0-1，必传）
     */
    @NotNull(message = "年龄风险因子总分不能为空")
    @Min(value = 0, message = "年龄风险因子总分不能小于0")
    @Max(value = 1, message = "年龄风险因子总分不能大于1")
    private BigDecimal ageRiskValue;

    /**
     * 职业风险因子总分（0-1，必传）
     */
    @NotNull(message = "职业风险因子总分不能为空")
    @Min(value = 0, message = "职业风险因子总分不能小于0")
    @Max(value = 1, message = "职业风险因子总分不能大于1")
    private BigDecimal occupationRiskValue;

    /**
     * 健康风险因子总分（0-1，必传）
     */
    @NotNull(message = "健康风险因子总分不能为空")
    @Min(value = 0, message = "健康风险因子总分不能小于0")
    @Max(value = 1, message = "健康风险因子总分不能大于1")
    private BigDecimal healthRiskValue;

    /**
     * 保额风险因子总分（0-1，必传）
     */
    @NotNull(message = "保额风险因子总分不能为空")
    @Min(value = 0, message = "保额风险因子总分不能小于0")
    @Max(value = 1, message = "保额风险因子总分不能大于1")
    private BigDecimal amountRiskValue;

    /**
     * 总风险因子总分（0-1，必传）
     */
    @NotNull(message = "总风险因子总分不能为空")
    @Min(value = 0, message = "总风险因子总分不能小于0")
    @Max(value = 1, message = "总 风险因子总分不能大于1")
    private BigDecimal totalRiskValue;
}
