package org.allen.ins.underwriting.rc.factor.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 风控因子计算请求DTO（承保风控核心接口入参）
 */
@Data
public class RiskFactorCalculateDTO {
    /**
     * 投保人ID（必传，关联投保人信息）
     */
    @NotNull(message = "投保人ID不能为空")
    private Long policyHolderId;

    /**
     * 投保金额（必传，影响保额风险因子）
     */
    @NotNull(message = "投保金额不能为空")
    private BigDecimal insureAmount;

    /**
     * 核保申请单号（必传，区分同一投保人的多次核保）
     */
    @NotNull(message = "核保申请单号不能为空")
    // TODO 暂未使用，预留用于区分同一投保人的多次核保请求
    private Long underwritingApplyId;
}