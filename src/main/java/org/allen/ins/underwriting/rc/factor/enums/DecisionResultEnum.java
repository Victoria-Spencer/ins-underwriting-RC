package org.allen.ins.underwriting.rc.factor.enums;

import lombok.Getter;

/**
 * 风险决策结果枚举
 * 匹配业务场景：承保/拒保/AI复查后定价
 */
@Getter
public enum DecisionResultEnum {
    /**
     * 直接承保
     */
    DIRECT_ACCEPT("承保", "DIRECT_ACCEPT"),
    /**
     * 直接拒保
     */
    DIRECT_REJECT("拒保", "DIRECT_REJECT");

    // 中文展示名（前端/日志使用）
    private final String chineseName;
    // 英文编码（数据库/接口传输使用）
    private final String englishCode;

    DecisionResultEnum(String chineseName, String englishCode) {
        this.chineseName = chineseName;
        this.englishCode = englishCode;
    }

    // 根据中文名称反向查找枚举（兼容原字符串常量使用场景）
    public static DecisionResultEnum getByChineseName(String chineseName) {
        for (DecisionResultEnum enumVal : values()) {
            if (enumVal.chineseName.equals(chineseName)) {
                return enumVal;
            }
        }
        throw new IllegalArgumentException("无效的决策结果中文名称：" + chineseName);
    }

    // 根据英文编码反向查找枚举
    public static DecisionResultEnum getByEnglishCode(String englishCode) {
        for (DecisionResultEnum enumVal : values()) {
            if (enumVal.englishCode.equals(englishCode)) {
                return enumVal;
            }
        }
        throw new IllegalArgumentException("无效的决策结果英文编码：" + englishCode);
    }
}