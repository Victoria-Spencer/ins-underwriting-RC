package org.allen.ins.underwriting.rc.decision.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RiskAIAnalysisRequest {

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
    @Max(value = 1, message = "总风险因子总分不能大于1")
    private BigDecimal totalRiskValue;
}
