package org.allen.ins.underwriting.rc.decision.pojo.vo;

import lombok.Data;

/**
 * 承保决策结果VO
 */
@Data
public class RiskDecisionVO {
    /**
     * 决策结果（承保/拒保/加费承保/核保中）
     */
    private String decisionResult;

    /**
     * 决策依据（前端展示用，精简）
     */
    private String decisionReason;

    /**
     * 加费比例（仅加费承保时返回，如：20%）
     */
    private String extraFeeRatio;
}