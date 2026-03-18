package org.allen.ins.underwriting.pojo.vo;

import lombok.Data;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingVO;

/**
 * 承保风控核心响应VO（返回给前端的完整结果）
 */
@Data
public class UnderwritingResponseVO {
    /**
     * 投保人基本信息
     */
    private PolicyHolderVO policyHolder;
    /**
     * 风险因子计算结果
     */
    private RiskFactorVO riskFactor;
    /**
     * 逆选择防控结果
     */
    private AntiselectionVO antiselection;
    /**
     * 风控决策结果
     */
    private RiskDecisionVO riskDecision;
    /**
     * 保费定价结果
     */
    private PricingVO pricing;
    /**
     * 最终承保结果（承保/拒保/加费）
     */
    private String underwritingResult;
}