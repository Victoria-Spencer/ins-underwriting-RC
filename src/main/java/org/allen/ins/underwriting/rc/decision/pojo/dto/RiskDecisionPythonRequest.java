package org.allen.ins.underwriting.rc.decision.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 调用Python风控决策API的请求DTO
 * 字段和Python端RiskDecisionRequest严格对齐（camelCase自动转snake_case）
 */
@Data
public class RiskDecisionPythonRequest {
    // TODO 硬编码改为通过反射读取数据库中的值
    /**
     * 业务线（如重疾险/医疗险，必传）
     */
    @NotBlank(message = "业务线不能为空")
    private String businessLine;

    /**
     * 健康因子总分（0-100，必传）
     */
    @NotNull(message = "健康因子总分不能为空")
    @Min(value = 0, message = "健康因子总分不能小于0")
    @Max(value = 100, message = "健康因子总分不能大于100")
    private Integer healthFactorTotal;

    /**
     * 职业风险等级（数值化：0=低，1=较低，2=中，3高，4极高，必传）
     */
    @NotNull(message = "职业风险等级不能为空")
    @Min(value = 0, message = "职业风险等级只能是0/1/2/3/4")
    @Max(value = 4, message = "职业风险等级只能是0/1/2/3/4")
    private Integer occupationRiskLevel;

    /**
     * 投保年龄（0-120，必传）
     */
    @NotNull(message = "投保年龄不能为空")
    @Min(value = 0, message = "投保年龄不能小于0岁")
    @Max(value = 120, message = "投保年龄不能大于120岁")
    private Integer age;

    /**
     * 投保保额（单位：元，必传）
     */
    @NotNull(message = "投保保额不能为空")
    private BigDecimal insureAmount;

    /**
     * 是否有既往病史（必传）
     */
    @NotNull(message = "是否有既往病史不能为空")
    private Boolean hasHistoryDisease;
}