package org.allen.ins.underwriting.pojo.domain.dict;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 职业风险字典实体类
 * 对应表：t_occupation_risk_dict
 *
 * @author allen
 * @date 2026-03-18
 */
@Data
@TableName("t_occupation_risk_dict")
public class OccupationRiskDict {
    private Long id;

    private String occupationCode; // 职业编码
    private String occupationName; // 职业名称
    private BigDecimal riskValue;  // 风险值
    private String riskLevel;      // 风险等级（低风险/中风险等）
    private Integer status;        // 是否启用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}