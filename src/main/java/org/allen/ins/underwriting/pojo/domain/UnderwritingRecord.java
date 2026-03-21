package org.allen.ins.underwriting.pojo.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 承保记录表（t_underwriting_record）实体类
 * 汇总整个承保流程结果，最终归档
 */
@Data
@Accessors(chain = true)
public class UnderwritingRecord implements Serializable {
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
     * 全链路追踪ID
     */
    private String traceId;

    /**
     * 关联风控决策记录ID
     */
    private Long riskDecisionId;

    /**
     * 关联保费定价记录ID
     */
    private Long pricingRecordId;

    /**
     * 承保结果（承保/拒保）
     */
    private String underwritingResult;

    /**
     * 承保时间
     */
    @TableField(value = "calculate_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime underwritingTime;

    /**
     * 操作人员（系统/人工核保员）
     */
    private String operator;
}