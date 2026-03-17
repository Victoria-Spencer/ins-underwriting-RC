package org.allen.ins.underwriting.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 承保风控核心请求DTO（前端调用聚合接口时传入）
 */
@Data
public class UnderwritingRequestDTO {
    /**
     * 投保人ID（必填，关联投保人表）
     */
    @NotNull(message = "投保人ID不能为空")
    private Long policyHolderId;

    /**
     * 投保金额（影响保额风险因子）
     */
    @NotNull(message = "投保金额不能为空")
    private BigDecimal insureAmount;
}