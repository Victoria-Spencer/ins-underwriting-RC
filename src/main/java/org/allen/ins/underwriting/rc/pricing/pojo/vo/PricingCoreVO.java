package org.allen.ins.underwriting.rc.pricing.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 保费定价结果VO
 */
@Data
public class PricingCoreVO {
    // **************** 输入参数回显（便于核对） ****************
    /** 投保保额（元） */
    private BigDecimal insureAmount;
    /** 风险概率（如0.001） */
    private BigDecimal riskProbability;
    /** 成本系数（如0.1） */
    private BigDecimal costCoefficient;
    /** 利润系数（如0.05） */
    private BigDecimal profitCoefficient;

    /** 预期赔付金额（元）= 保额 × 风险概率 */
    private BigDecimal expectedPayoutAmount;
    /** 基础保费（元）= 预期赔付金额 × (1 + 成本系数) */
    private BigDecimal basePremium;
    /** 最终保费（元）= 基础保费 × (1 + 利润系数) */
    private BigDecimal finalPremium;

    // **************** 计算明细（便于核保/客户咨询） ****************
    /** 定价公式明细（文字描述） */
    private String pricingFormulaDesc;
}