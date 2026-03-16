package org.allen.ins.underwriting.rc.factor.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 风险因子计算记录表（t_risk_factor_record）实体类
 * 记录风险因子计算过程和结果
 */
@Data
public class RiskFactorRecord implements Serializable {
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
     * 年龄风险因子（0-1）
     */
    private BigDecimal ageRiskValue;

    /**
     * 职业风险因子（0-1）
     */
    private BigDecimal occupationRiskValue;

    /**
     * 健康风险因子（0-1）
     */
    private BigDecimal healthRiskValue;

    /**
     * 保额风险因子（0-1）
     */
    private BigDecimal amountRiskValue;

    /**
     * 总风险因子值（0-1，各维度因子加权求和）
     */
    private BigDecimal totalRiskValue;

    /**
     * 计算时间
     */
    private LocalDateTime calculateTime;

    /**
     * 计算人（系统/人工）
     */
    private String calculatorUser;
}