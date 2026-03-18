package org.allen.ins.underwriting.rc.factor.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
     * 核保申请单号（必传，区分同一投保人的多次核保）
     */
    @NotNull(message = "核保申请单号不能为空")
    private Long underwritingApplyId;
}