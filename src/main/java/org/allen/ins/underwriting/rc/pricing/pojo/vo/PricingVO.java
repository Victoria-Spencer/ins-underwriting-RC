package org.allen.ins.underwriting.rc.pricing.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 保费定价结果VO
 */
@Data
public class PricingVO {
    /**
     * 基础保费
     */
    private BigDecimal basePremium;

    /**
     * 加费金额（无加费则为0）
     */
    private BigDecimal extraPremium;

    /**
     * 最终保费（基础+加费）
     */
    private BigDecimal finalPremium;

    /**
     * 保费有效期（如：2024-05-20 至 2025-05-19）
     */
    private String premiumValidPeriod;
}