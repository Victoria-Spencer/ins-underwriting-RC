package org.allen.ins.underwriting.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.allen.ins.underwriting.rc.antiselection.pojo.vo.AntiselectionVO;
import org.allen.ins.underwriting.rc.decision.pojo.vo.RiskDecisionVO;
import org.allen.ins.underwriting.rc.factor.pojo.vo.RiskFactorVO;
import org.allen.ins.underwriting.rc.pricing.pojo.vo.PricingCoreVO;

/**
 * 承保风控核心响应VO（返回给前端的完整结果）
 */
@Data
@Accessors(chain = true)
public class UnderwritingResponseVO {
    /**
     * 投保人基本信息
     */
    private PolicyHolderVO policyHolderVO;
    /**
     * 风险因子计算结果
     */
    private RiskFactorVO riskFactorVO;
    /**
     * 逆选择防控结果
     */
    private AntiselectionVO antiselectionVO;
    /**
     * 风控决策结果
     */
    private RiskDecisionVO riskDecisionVO;
    /**
     * 保费定价结果
     */
    private PricingCoreVO pricingVO;
    /**
     * 最终承保结果（承保/拒保）
     */
    private String underwritingResult;
}