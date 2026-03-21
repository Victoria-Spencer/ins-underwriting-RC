package org.allen.ins.underwriting.rc.decision.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 承保决策结果VO
 */
@Data
public class RiskDecisionVO {
    /**
     * 决策结果（承保/拒保）
     */
    private String decisionResult;

    /**
     * 决策依据（前端展示用，精简）
     */
    private String decisionReason;

    /**
     * 最终风险概率（0-1，dataModelProb <= 0.3或 >= 0.7则选data_model_prob；否则agent_prob）
     */
    private BigDecimal riskProb;

}