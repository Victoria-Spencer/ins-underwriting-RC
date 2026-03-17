package org.allen.ins.underwriting.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 职业风险字典表（t_occupation_risk_dict）实体类
 * 风险因子模块基础字典，量化职业风险
 */
@Data
public class OccupationRiskDict implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 职业名称（唯一）
     */
    private String occupationName;

    /**
     * 职业风险值（0-1，如：建筑工人=0.4，办公室职员=0.05）
     */
    private BigDecimal riskValue;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}