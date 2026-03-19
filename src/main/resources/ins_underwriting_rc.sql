CREATE DATABASE IF NOT EXISTS ins_underwriting_rc;

USE ins_underwriting_rc;

-- 投保人表
CREATE TABLE  IF NOT EXISTS `t_policy_holder` (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `name` varchar(32) NOT NULL COMMENT '投保人姓名',
                                   `id_card` varchar(18) NOT NULL COMMENT '身份证号',
                                   `age` tinyint(4) NOT NULL COMMENT '年龄',
                                   `occupation` varchar(64) NOT NULL COMMENT '职业（关联t_occupation_risk_dict.occupation_name）',
                                   `health_info` text COMMENT '健康告知（非标信息）',
                                   `phone` varchar(11) NOT NULL COMMENT '手机号',
                                   `insurance_type` varchar(32) COMMENT '投保险种类型（意外险/健康险/寿险）',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                   `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记（0=未删，1=已删）',
                                   `ext_json` varchar(512) COMMENT '扩展字段（JSON格式）',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_id_card` (`id_card`) COMMENT '身份证号唯一索引',
                                   KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投保人核心信息表';



-- 职业风险字典表（t_occupation_risk_dict）
CREATE TABLE IF NOT EXISTS `t_occupation_risk_dict` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                `occupation_code` varchar(32) NOT NULL COMMENT '职业编码（101/102/201…）',
                                `occupation_name` varchar(64) NOT NULL COMMENT '职业名称（办公室职员/教师…）',
                                `risk_value` decimal(3,2) NOT NULL COMMENT '职业风险值（0-1）',
                                `risk_level` varchar(16) NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
                                `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_occupation_code` (`occupation_code`) COMMENT '职业编码唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职业风险字典表';


-- 年龄风险字典表（保留CHECK约束，去掉CHECK后的COMMENT）
CREATE TABLE IF NOT EXISTS `t_age_risk_dict` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `age_min` tinyint(3) NOT NULL COMMENT '年龄下限（含）',
    `age_max` tinyint(3) NOT NULL COMMENT '年龄上限（含）',
    `risk_value` decimal(3,2) NOT NULL COMMENT '年龄风险值（0-1）',
    `risk_level` varchar(16) NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_age_range` (`age_min`,`age_max`) COMMENT '年龄区间唯一索引（避免重复区间）',
    CONSTRAINT `ck_age_range` CHECK (`age_min` <= `age_max`) -- 这里去掉了COMMENT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年龄风险字典表（校验规则：年龄下限≤上限）'; -- 把校验规则写在表注释里

-- 保额风险字典表（保留CHECK约束，去掉CHECK后的COMMENT）
CREATE TABLE IF NOT EXISTS `t_sum_insured_risk_dict` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `sum_insured_min` decimal(16,2) NOT NULL COMMENT '保额下限（含，单位：元）',
    `sum_insured_max` decimal(16,2) NOT NULL COMMENT '保额上限（含，单位：元）',
    `risk_value` decimal(3,2) NOT NULL COMMENT '保额风险值（0-1）',
    `risk_level` varchar(16) NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sum_insured_range` (`sum_insured_min`,`sum_insured_max`) COMMENT '保额区间唯一索引',
    CONSTRAINT `ck_sum_insured_range` CHECK (`sum_insured_min` <= `sum_insured_max`) -- 这里去掉了COMMENT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='保额风险字典表（校验规则：保额下限≤上限）'; -- 把校验规则写在表注释里



-- 风险因子计算记录表
CREATE TABLE IF NOT EXISTS `t_risk_factor_record` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `policy_holder_id` bigint(20) NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                        `age_risk_value` decimal(3,2) NOT NULL COMMENT '年龄风险因子（0-1）',
                                        `occupation_risk_value` decimal(3,2) NOT NULL COMMENT '职业风险因子（0-1）',
                                        `health_risk_value` decimal(3,2) NOT NULL COMMENT '健康风险因子（0-1）',
                                        `amount_risk_value` decimal(3,2) NOT NULL COMMENT '保额风险因子（0-1）',
                                        `total_risk_value` decimal(3,2) NOT NULL COMMENT '总风险因子值（0-1）',
                                        `calculate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
                                        `calculator_user` varchar(32) NOT NULL DEFAULT 'system' COMMENT '计算人（系统/人工）',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_policy_holder_id` (`policy_holder_id`) COMMENT '投保人ID索引',
                                        KEY `idx_calculate_time` (`calculate_time`) COMMENT '计算时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险因子计算记录表';


-- 逆选择防控记录表
CREATE TABLE IF NOT EXISTS `t_antiselection_record` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `policy_holder_id` bigint(20) NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                          `risk_factor_id` bigint(20) NOT NULL COMMENT '关联风险因子记录ID（t_risk_factor_record.id）',
                                          `anti_rule_name` varchar(64) COMMENT '触发的逆选择规则名称',
                                          `anti_risk_level` varchar(16) COMMENT '逆选择风险等级（高/中/低）',
                                          `anti_result` varchar(256) COMMENT '逆选择判断结果',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_policy_holder_id` (`policy_holder_id`) COMMENT '投保人ID索引',
                                          KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='逆选择防控记录表（风控核心）';


-- 风控决策记录表
CREATE TABLE IF NOT EXISTS `t_risk_decision_record` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `policy_holder_id` bigint(20) NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                          `anti_record_id` bigint(20) NOT NULL COMMENT '关联逆选择记录ID（t_antiselection_record.id）',
                                          `data_model_prob` decimal(4,3) NOT NULL COMMENT '数据模型风险概率（0-1，70%权重）',
                                          `agent_prob` decimal(4,3) NOT NULL COMMENT '智能体（豆包）风险概率（0-1，30%权重）',
                                          `final_risk_prob` decimal(4,3) NOT NULL COMMENT '最终风险概率（0-1）',
                                          `risk_decision` varchar(16) NOT NULL COMMENT '风控决策（承保/拒保/加费/人工核保）',
                                          `decision_reason` varchar(256) COMMENT '决策原因',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          PRIMARY KEY (`id`),
                                          KEY `idx_policy_holder_id` (`policy_holder_id`) COMMENT '投保人ID索引',
                                          KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风控决策记录表（金融审计核心）';

-- 保费定价记录表
CREATE TABLE IF NOT EXISTS `t_pricing_record` (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `policy_holder_id` bigint(20) NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                    `risk_decision_id` bigint(20) NOT NULL COMMENT '关联风控决策记录ID（t_risk_decision_record.id）',
                                    `base_premium` decimal(10,2) NOT NULL COMMENT '基础保费',
                                    `risk_adjust_ratio` decimal(3,2) NOT NULL COMMENT '风险调整系数',
                                    `final_premium` decimal(10,2) NOT NULL COMMENT '最终保费',
                                    `pricing_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '定价时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_policy_holder_id` (`policy_holder_id`) COMMENT '投保人ID索引',
                                    KEY `idx_pricing_time` (`pricing_time`) COMMENT '定价时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='保费定价记录表';


-- 承保记录表
CREATE TABLE IF NOT EXISTS `t_underwriting_record` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `policy_holder_id` bigint(20) NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                         `risk_decision_id` bigint(20) NOT NULL COMMENT '关联风控决策记录ID（t_risk_decision_record.id）',
                                         `pricing_record_id` bigint(20) NOT NULL COMMENT '关联保费定价记录ID（t_pricing_record.id）',
                                         `underwriting_result` varchar(16) NOT NULL COMMENT '承保结果（承保/拒保/加费）',
                                         `underwriting_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '承保时间',
                                         `operator` varchar(32) NOT NULL DEFAULT 'system' COMMENT '操作人员（系统/人工核保员）',
                                         PRIMARY KEY (`id`),
                                         KEY `idx_policy_holder_id` (`policy_holder_id`) COMMENT '投保人ID索引',
                                         KEY `idx_underwriting_time` (`underwriting_time`) COMMENT '承保时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='承保记录总表（最终归档）';



-- 给保费定价记录表补充生效/失效时间
ALTER TABLE `t_pricing_record`
    ADD COLUMN `premium_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保费生效时间' AFTER `final_premium`,
ADD COLUMN `premium_end_time` datetime NOT NULL COMMENT '保费失效时间' AFTER `premium_start_time`;



/*-- 1. 新增险种规则表（存储不同险种的默认保障期间）
CREATE TABLE IF NOT EXISTS `t_insurance_type_rule` (
                                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `insurance_type` varchar(32) NOT NULL COMMENT '投保险种类型（意外险/健康险/寿险）',
    `default_period` int(11) NOT NULL COMMENT '默认保障期间（月）',
    `period_unit` varchar(8) NOT NULL DEFAULT 'month' COMMENT '期间单位（month/year）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_insurance_type` (`insurance_type`) COMMENT '险种类型唯一索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='险种规则表（存储保障期间等基础规则）';

-- 2. 初始化险种规则（示例：意外险1年、健康险6个月、寿险5年）
INSERT INTO `t_insurance_type_rule` (`insurance_type`, `default_period`)
VALUES ('意外险', 12), ('健康险', 6), ('寿险', 60);

-- 3. 给定价表补充生效时间，并关联险种规则推导失效时间
ALTER TABLE `t_pricing_record`
    ADD COLUMN `premium_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保费生效时间' AFTER `final_premium`,
ADD COLUMN `insurance_type_rule_id` bigint(20) NOT NULL COMMENT '关联险种规则ID' AFTER `premium_start_time`;

-- 4. 新增索引（优化关联查询）
ALTER TABLE `t_pricing_record` ADD KEY `idx_insurance_type_rule_id` (`insurance_type_rule_id`);*/



-- 1. 风险因子计算记录表加 trace_id
ALTER TABLE `t_risk_factor_record`
    ADD COLUMN `trace_id` varchar(64) NOT NULL COMMENT '全链路追踪ID（单次核保请求唯一标识）' AFTER `policy_holder_id`,
    ADD KEY `idx_trace_id` (`trace_id`) COMMENT 'trace_id索引（快速查询单次流程）';

-- 2. 逆选择防控记录表加 trace_id
ALTER TABLE `t_antiselection_record`
    ADD COLUMN `trace_id` varchar(64) NOT NULL COMMENT '全链路追踪ID' AFTER `policy_holder_id`,
    ADD KEY `idx_trace_id` (`trace_id`) COMMENT 'trace_id索引';

-- 3. 风控决策记录表加 trace_id
ALTER TABLE `t_risk_decision_record`
    ADD COLUMN `trace_id` varchar(64) NOT NULL COMMENT '全链路追踪ID' AFTER `policy_holder_id`,
    ADD KEY `idx_trace_id` (`trace_id`) COMMENT 'trace_id索引';

-- 4. 保费定价记录表加 trace_id
ALTER TABLE `t_pricing_record`
    ADD COLUMN `trace_id` varchar(64) NOT NULL COMMENT '全链路追踪ID' AFTER `policy_holder_id`,
    ADD KEY `idx_trace_id` (`trace_id`) COMMENT 'trace_id索引';

-- 5. 承保记录表加 trace_id
ALTER TABLE `t_underwriting_record`
    ADD COLUMN `trace_id` varchar(64) NOT NULL COMMENT '全链路追踪ID' AFTER `policy_holder_id`,
    ADD KEY `idx_trace_id` (`trace_id`) COMMENT 'trace_id索引';