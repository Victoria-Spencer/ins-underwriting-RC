package org.allen.ins.underwriting.rc.decision.enums;

import lombok.Getter;

// 决策枚举（标准化）
@Getter
public enum RiskDecisionEnum {
    ACCEPT("ACCEPT", "承保"),
    REJECT("REJECT", "拒保"),
    REVIEW("REVIEW", "人工复核");

    private final String code;
    private final String desc;

    RiskDecisionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}