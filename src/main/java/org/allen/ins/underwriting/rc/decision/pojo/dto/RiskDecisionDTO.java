package org.allen.ins.underwriting.rc.decision.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 承保决策请求DTO（风控因子计算完成后，内部调用决策接口用）
 */
@Data
@Accessors(chain = true)
public class RiskDecisionDTO {
    /**
     * 投保人ID
     */
    private Long policyHolderId;

    /**
     * 总风险因子值
     */
    private BigDecimal totalRiskValue;

    /**
     * 投保保额
     */
    private BigDecimal insureAmount;
}