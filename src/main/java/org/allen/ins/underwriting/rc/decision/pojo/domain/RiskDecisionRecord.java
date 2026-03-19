package org.allen.ins.underwriting.rc.decision.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 风控决策记录表（t_risk_decision_record）实体类
 * 记录最终风险决策过程，金融审计核心表
 */
@Data
public class RiskDecisionRecord implements Serializable {
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
     * 全链路追踪ID
     */
    private String traceId;

    /**
     * 关联逆选择记录ID
     */
    private Long antiRecordId;

    /**
     * 数据模型风险概率（0-1，70%权重）
     */
    private BigDecimal dataModelProb;

    /**
     * 智能体（豆包）风险概率（0-1，30%权重）
     */
    private BigDecimal agentProb;

    /**
     * 最终风险概率（0-1，data_model_prob*0.7 + agent_prob*0.3）
     */
    private BigDecimal finalRiskProb;

    /**
     * 风控决策（承保/拒保/加费/人工核保）
     */
    private String riskDecision;

    /**
     * 决策原因（如：“最终风险概率0.85，触发拒保”）
     */
    private String decisionReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

//    /**
//     * 新增：核保申请单号（外键关联t_underwriting_apply.id）
//     * 唯一标识一次核保申请，同一投保人多次申请会有不同的applyId
//     */
//    private Long underwritingApplyId;
//
//    /**
//     * 新增：核保批次/版本号（可选，便于业务识别）
//     * 比如“20240520-01”表示2024-05-20该投保人的第1次核保
//     */
//    private String underwritingBatchNo;
//
//    /**
//     * 新增：记录状态（可选，如INIT/COMPLETE/FAIL）
//     * 标记本次风险计算是否完成，便于筛选有效记录
//     */
//    private String recordStatus;
}