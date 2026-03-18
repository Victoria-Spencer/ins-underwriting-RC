package org.allen.ins.underwriting.pojo.domain.dict;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保额风险字典实体类
 * 对应表：t_sum_insured_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Data
@TableName("t_sum_insured_risk_dict")
public class SumInsuredRiskDict {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 保额下限（含，单位：元）
     */
    @TableField("sum_insured_min")
    private BigDecimal sumInsuredMin;

    /**
     * 保额上限（含，单位：元）
     */
    @TableField("sum_insured_max")
    private BigDecimal sumInsuredMax;

    /**
     * 保额风险值（0-1）
     */
    @TableField("risk_value")
    private BigDecimal riskValue;

    /**
     * 风险等级（低/较低/中/高/极高风险）
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 是否启用（1=是，0=否）
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间（MyBatis-Plus自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（MyBatis-Plus自动填充，插入+更新时触发）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}