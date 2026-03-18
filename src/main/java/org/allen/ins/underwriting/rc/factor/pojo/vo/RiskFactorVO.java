package org.allen.ins.underwriting.rc.factor.pojo.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风控因子计算结果VO（返回给前端，仅展示核心结果）
 */
@Data
public class RiskFactorVO {
    /**
     * 投保人ID
     */
    private Long policyHolderId;

    /**
     * 总风险因子值（0-1，核心结果）
     */
    private BigDecimal totalRiskValue;

    /**
     * 风险等级（前端展示用：低/中/高/极高）
     */
    private String totalRiskLevel;

    /**
     * 计算时间
     */
    private LocalDateTime calculateTime;

    // 注：不返回各维度因子（年龄/职业/健康/保额），除非前端需要精细化展示
}