package org.allen.ins.underwriting.rc.decision.constant;

import java.math.BigDecimal;

public final class RiskConstant {
    // Python评分阈值（0-100分）
    public static final Integer PYTHON_ACCEPT_THRESHOLD = 30; // <30分直接承保
    public static final Integer PYTHON_REJECT_THRESHOLD = 70; // >70分直接拒保
    // AI决策阈值（仅中间区间用）
    public static final Integer AI_ACCEPT_THRESHOLD = 40;
    public static final Integer AI_REJECT_THRESHOLD = 60;
    // Python概率转评分的系数
    public static final BigDecimal PROBABILITY_TO_SCORE = new BigDecimal("100");
}