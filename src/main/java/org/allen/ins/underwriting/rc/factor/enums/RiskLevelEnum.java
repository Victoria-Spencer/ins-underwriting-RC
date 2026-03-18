package org.allen.ins.underwriting.rc.factor.enums;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 风险等级枚举（结构化管理等级名称+阈值规则）
 */
public enum RiskLevelEnum {
    /** 低风险：总风险值 < 0.3 */
    LOW("低风险", BigDecimal.ZERO, new BigDecimal("0.3")),
    /** 中风险：0.3 ≤ 总风险值 < 0.6 */
    MEDIUM("中风险", new BigDecimal("0.3"), new BigDecimal("0.6")),
    /** 高风险：总风险值 ≥ 0.6 */
    HIGH("高风险", new BigDecimal("0.6"), BigDecimal.ONE);

    // getter
    // 等级名称（前端展示用）
    @Getter
    private final String levelName;
    // 阈值下限（含）
    private final BigDecimal minValue;
    // 阈值上限（不含）
    private final BigDecimal maxValue;

    // 构造方法
    RiskLevelEnum(String levelName, BigDecimal minValue, BigDecimal maxValue) {
        this.levelName = levelName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * 核心方法：根据总风险值匹配对应的风险等级
     * @param totalRiskValue 总风险因子值（0-1）
     * @return 风险等级枚举
     */
    public static RiskLevelEnum getByTotalValue(BigDecimal totalRiskValue) {
        // 空值兜底：默认低风险（或抛异常，根据业务定）
        if (null == totalRiskValue) {
            return LOW;
        }
        // 遍历匹配阈值
        for (RiskLevelEnum level : values()) {
            // 条件：min ≤ value < max（最后一个HIGH的max是1，包含等于1的情况）
            boolean match = totalRiskValue.compareTo(level.minValue) >= 0
                    && (level == HIGH || totalRiskValue.compareTo(level.maxValue) < 0);
            if (match) {
                return level;
            }
        }
        // 兜底：超出范围（如>1）按高风险算
        return HIGH;
    }

}