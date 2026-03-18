package org.allen.ins.underwriting.pojo.domain.dict;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 年龄风险字典实体类
 * 对应表：t_age_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Data
@TableName("t_age_risk_dict")
public class AgeRiskDict {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 年龄下限（含）
     */
    @TableField("age_min")
    private Integer ageMin;

    /**
     * 年龄上限（含）
     */
    @TableField("age_max")
    private Integer ageMax;

    /**
     * 年龄风险值（0-1）
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