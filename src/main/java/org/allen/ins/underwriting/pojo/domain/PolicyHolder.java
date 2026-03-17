package org.allen.ins.underwriting.pojo.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 投保人表（t_policy_holder）实体类
 * 核心基础表，存储投保人核心信息
 */
@Data
public class PolicyHolder implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 投保人姓名
     */
    private String name;

    /**
     * 身份证号（唯一索引）
     */
    private String idCard;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 职业（关联职业风险字典表）
     */
    private String occupation;

    /**
     * 健康告知（非标信息，如：是否有住院史）
     */
    private String healthInfo;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 投保险种类型（如：意外险、健康险、寿险）
     */
    private String insuranceType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记（0=未删，1=已删）
     */
    private Integer deleteFlag;

    /**
     * 扩展字段（JSON格式，存储非标/临时信息）
     */
    private String extJson;
}