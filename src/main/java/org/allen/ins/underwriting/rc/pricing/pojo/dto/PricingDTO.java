package org.allen.ins.underwriting.rc.pricing.pojo.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 保费定价请求DTO（决策完成后，内部调用定价接口用）
 */
@Data
public class PricingDTO {
    /**
     * 投保人ID
     */
    private Long policyHolderId;

    /**
     * 承保决策结果
     */
    private String decisionResult;

    /**
     * 加费比例（无加费则为0）
     */
    private BigDecimal extraFeeRatio;

    /**
     * 投保保额
     */
    private BigDecimal insureAmount;
}