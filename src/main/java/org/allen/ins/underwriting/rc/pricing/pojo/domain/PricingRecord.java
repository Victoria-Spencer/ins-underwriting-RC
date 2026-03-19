package org.allen.ins.underwriting.rc.pricing.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 保费定价记录表（t_pricing_record）实体类
 * 记录保费计算结果
 */
@Data
public class PricingRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 投保人ID（外键关联t_policy_holder.id）
     */
    private Long policyHolderId;

    /**
     * 全链路追踪ID
     */
    private String traceId;

    /**
     * 关联风控决策记录ID
     */
    private Long riskDecisionId;

    /**
     * 基础保费
     */
    private BigDecimal basePremium;

    /**
     * 风险调整系数（如：1.5=上浮50%，0.9=下浮10%）
     */
    private BigDecimal riskAdjustRatio;

    /**
     * 最终保费（base_premium * risk_adjust_ratio）
     */
    private BigDecimal finalPremium;

    /**
     * 定价时间
     */
    private LocalDateTime pricingTime;

    /**
     * 保费生效时间
     */
    private LocalDateTime premium_start_time;

    /**
     * 保费失效时间
     */
    private LocalDateTime premium_end_time;
}