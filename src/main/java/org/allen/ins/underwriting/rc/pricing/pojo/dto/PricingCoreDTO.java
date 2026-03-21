package org.allen.ins.underwriting.rc.pricing.pojo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 保费定价请求DTO（决策完成后，内部调用定价接口用）
 */
@Data
@Accessors(chain = true)
public class PricingCoreDTO {
    /**
     * 投保人ID
     */
    private Long policyHolderId;

    /**
     * 投保保额（元）：核心基数，必须≥1000元（保险行业最低保额限制）
     */
    @NotNull(message = "投保保额不能为空")
//    @Min(value = 1000, message = "投保保额不能低于1000元")
    private BigDecimal insureAmount;

    /**
     * 风险概率：精算给出的出险概率（如0.001=0.1%），必须>0且≤1
     */
    @NotNull(message = "风险概率不能为空")
    @DecimalMin(value = "0", message = "风险概率必须大于0（如0.001=0.1%）")
    @DecimalMax(value = "1", message = "风险概率不能大于1（否则保费超过保额）")
    @Digits(integer = 1, fraction = 6, message = "风险概率最多保留6位小数（如0.001234）")
    private BigDecimal riskProbability;

    /**
     * 成本系数：运营成本占比（如0.1=10%），必须≥0
     */
    @NotNull(message = "成本系数不能为空")
    @DecimalMin(value = "0", inclusive = true, message = "成本系数不能为负数")
    @Digits(integer = 1, fraction = 6, message = "成本系数最多保留6位小数（如0.12346）")
    private BigDecimal costCoefficient;

    /**
     * 利润系数：预期利润率（如0.05=5%），必须≥0
     */
    @NotNull(message = "利润系数不能为空")
    @DecimalMin(value = "0", inclusive = true, message = "利润系数不能为负数")
    @Digits(integer = 1, fraction = 6, message = "利润系数最多保留3位小数（如0.056789）")
    private BigDecimal profitCoefficient;
}