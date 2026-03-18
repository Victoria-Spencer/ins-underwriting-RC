package org.allen.ins.underwriting.rc.decision.pojo.vo;

import lombok.Data;

/**
 * 调用Python风控决策API的响应DTO
 * 字段和Python端RiskDecisionResponse严格对齐
 */
@Data
public class RiskDecisionPythonResponse {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 轨迹ID（唯一标识，供审计/回溯）
     */
    private String traceId;

    /**
     * 最终决策结果（PASS=通过，REJECT=拒绝，REVIEW=人工复核）
     */
    private String finalDecision;

    /**
     * 拒绝/复核原因
     */
    private String rejectReason;

    /**
     * 拒保概率（0-1，保留4位小数）
     */
    private Double rejectProbability;
}