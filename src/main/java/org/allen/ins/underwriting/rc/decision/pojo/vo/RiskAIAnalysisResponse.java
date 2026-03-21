package org.allen.ins.underwriting.rc.decision.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增：AI复查响应类（补充缺失定义）
 */
@Data
public class RiskAIAnalysisResponse {
    private String finalDecision;       // AI复查最终决策（承保/拒保）
    private BigDecimal agentRiskProb;   // AI复查风险概率
    private String riskAnalysis;        // AI侧风险评估说明（仅供参考）
    private String reviewConclusion;    // AI复查结论
}