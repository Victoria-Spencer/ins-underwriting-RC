package org.allen.ins.underwriting.rc.factor.constant;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// 结构化常量类
public final class FactorWeightConst {

    // 用Map结构化存储，而非单个常量（如FACTOR_SMOKE=0.8）
    private static final Map<String, BigDecimal> WEIGHT_MAP;

    // 静态初始化：集中管理所有权重
    static {
        // 保额因子
        WEIGHT_MAP = Map.of(
                "RISK_FACTOR_AGE", new BigDecimal("0.2"),       // 权重（年龄风险因子）
                "RISK_FACTOR_OCCUPATION", new BigDecimal("0.2"),    // 权重（职业风险因子）
                "RISK_FACTOR_AMOUNT", new BigDecimal("0.3"),        // 权重（保额风险因子）
                "RISK_FACTOR_HEALTH", new BigDecimal("0.3")         // 权重（健康风险因子）
        );
    }

    // 封装获取方法，而非直接暴露Map
    public static BigDecimal getWeight(String factorCode) {
        // 兜底默认值：避免空指针，统一容错
        return WEIGHT_MAP.getOrDefault(factorCode, BigDecimal.ONE);
    }
}
