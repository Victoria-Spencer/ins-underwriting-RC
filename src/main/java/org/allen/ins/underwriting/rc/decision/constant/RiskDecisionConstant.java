package org.allen.ins.underwriting.rc.decision.constant;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 风险决策常量类
 * 常量注释 + 统一小数精度（保留2位）
 */
public final class RiskDecisionConstant {
    private RiskDecisionConstant() {
        // 私有构造，禁止实例化
    }

    /**
     * 直接承保阈值上限（0-0.30）
     */
    public static final BigDecimal DIRECT_ACCEPT_THRESHOLD = new BigDecimal("0.3").setScale(2, RoundingMode.HALF_UP);

    /**
     * 直接拒保阈值下限（0.70-1.00）
     */
    public static final BigDecimal DIRECT_REJECT_THRESHOLD = new BigDecimal("0.7").setScale(2, RoundingMode.HALF_UP);
}