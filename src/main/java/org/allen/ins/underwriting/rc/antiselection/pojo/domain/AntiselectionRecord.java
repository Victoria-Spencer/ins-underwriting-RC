package org.allen.ins.underwriting.rc.antiselection.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 逆选择防控记录表（t_antiselection_record）实体类
 * 记录逆选择判断结果，风控决策核心依据
 */
@Data
public class AntiselectionRecord implements Serializable {
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
     * 关联风险因子记录ID
     */
    private Long riskFactorId;

    /**
     * 触发的逆选择规则名称（如：“隐瞒住院记录”）
     */
    private String antiRuleName;

    /**
     * 逆选择风险等级（高/中/低）
     */
    private String antiRiskLevel;

    /**
     * 逆选择判断结果（详细描述）
     */
    private String antiResult;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}