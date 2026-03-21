/*
 Navicat Premium Dump SQL

 Source Server         : 主机
 Source Server Type    : MySQL
 Source Server Version : 90200 (9.2.0)
 Source Host           : localhost:3306
 Source Schema         : ins_underwriting_rc

 Target Server Type    : MySQL
 Target Server Version : 90200 (9.2.0)
 File Encoding         : 65001

 Date: 21/03/2026 17:27:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_age_risk_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_age_risk_dict`;
CREATE TABLE `t_age_risk_dict`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                    `age_min` tinyint NOT NULL COMMENT '年龄下限（含）',
                                    `age_max` tinyint NOT NULL COMMENT '年龄上限（含）',
                                    `risk_value` decimal(3, 2) NOT NULL COMMENT '年龄风险值（0-1）',
                                    `risk_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
                                    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_age_range`(`age_min` ASC, `age_max` ASC) USING BTREE COMMENT '年龄区间唯一索引（避免重复区间）',
                                    CONSTRAINT `ck_age_range` CHECK (`age_min` <= `age_max`)
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '年龄风险字典表（校验规则：年龄下限≤上限）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_age_risk_dict
-- ----------------------------
INSERT INTO `t_age_risk_dict` VALUES (1, 0, 17, 0.20, '较低风险', 1, '2026-03-18 16:14:02', '2026-03-19 19:43:28');
INSERT INTO `t_age_risk_dict` VALUES (2, 18, 35, 0.10, '低风险', 1, '2026-03-18 16:14:02', '2026-03-19 19:43:26');
INSERT INTO `t_age_risk_dict` VALUES (3, 36, 50, 0.40, '中风险', 1, '2026-03-18 16:14:02', '2026-03-18 16:14:02');
INSERT INTO `t_age_risk_dict` VALUES (4, 51, 65, 0.70, '高风险', 1, '2026-03-18 16:14:02', '2026-03-18 16:14:02');
INSERT INTO `t_age_risk_dict` VALUES (5, 66, 120, 0.90, '极高风险', 1, '2026-03-18 16:14:02', '2026-03-18 16:14:02');

-- ----------------------------
-- Table structure for t_antiselection_record
-- ----------------------------
DROP TABLE IF EXISTS `t_antiselection_record`;
CREATE TABLE `t_antiselection_record`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `policy_holder_id` bigint NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                           `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全链路追踪ID',
                                           `risk_factor_id` bigint NOT NULL COMMENT '关联风险因子记录ID（t_risk_factor_record.id）',
                                           `anti_rule_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '触发的逆选择规则名称',
                                           `anti_risk_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '逆选择风险等级（高/中/低）',
                                           `anti_result` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '逆选择判断结果',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `idx_policy_holder_id`(`policy_holder_id` ASC) USING BTREE COMMENT '投保人ID索引',
                                           INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
                                           INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE COMMENT 'trace_id索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '逆选择防控记录表（风控核心）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_antiselection_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_occupation_risk_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_occupation_risk_dict`;
CREATE TABLE `t_occupation_risk_dict`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                           `occupation_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职业编码（101/102/201…）',
                                           `occupation_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职业名称（办公室职员/教师…）',
                                           `risk_value` decimal(3, 2) NOT NULL COMMENT '职业风险值（0-1）',
                                           `risk_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
                                           `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE INDEX `uk_occupation_code`(`occupation_code` ASC) USING BTREE COMMENT '职业编码唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '职业风险字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_occupation_risk_dict
-- ----------------------------
INSERT INTO `t_occupation_risk_dict` VALUES (1, '101', '办公室职员', 0.10, '低风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (2, '102', '教师', 0.10, '低风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (3, '103', '医生', 0.10, '低风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (4, '201', '销售', 0.40, '较低风险', 1, '2026-03-18 15:27:24', '2026-03-20 13:14:07');
INSERT INTO `t_occupation_risk_dict` VALUES (5, '202', '服务员', 0.40, '较低风险', 1, '2026-03-18 15:27:24', '2026-03-20 13:14:12');
INSERT INTO `t_occupation_risk_dict` VALUES (6, '203', '文员', 0.40, '较低风险', 1, '2026-03-18 15:27:24', '2026-03-20 13:14:16');
INSERT INTO `t_occupation_risk_dict` VALUES (7, '301', '司机', 0.50, '中风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (8, '302', '快递员', 0.50, '中风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (9, '303', '厨师', 0.50, '中风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (10, '401', '建筑工人', 0.70, '高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (11, '402', '电工', 0.70, '高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (12, '403', '焊工', 0.70, '高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (13, '501', '高空作业人员', 0.90, '极高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (14, '502', '矿工', 0.90, '极高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');
INSERT INTO `t_occupation_risk_dict` VALUES (15, '503', '消防员', 0.90, '极高风险', 1, '2026-03-18 15:27:24', '2026-03-18 15:27:24');

-- ----------------------------
-- Table structure for t_policy_holder
-- ----------------------------
DROP TABLE IF EXISTS `t_policy_holder`;
CREATE TABLE `t_policy_holder`  (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                    `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '投保人姓名',
                                    `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '身份证号',
                                    `age` tinyint NOT NULL COMMENT '年龄',
                                    `occupation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '职业（关联t_occupation_risk_dict.occupation_name）',
                                    `health_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '健康告知（非标信息）',
                                    `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
                                    `insurance_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '投保险种类型（意外险/健康险/寿险）',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                    `delete_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标记（0=未删，1=已删）',
                                    `ext_json` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '扩展字段（JSON格式）',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE COMMENT '身份证号唯一索引',
                                    INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '投保人核心信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_policy_holder
-- ----------------------------
INSERT INTO `t_policy_holder` VALUES (1, '御国良', '350304200002020101', 27, '医生', '健康', '15759362000', '健康保险', '2026-03-21 17:25:20', '2026-03-21 17:26:55', 0, NULL);

-- ----------------------------
-- Table structure for t_pricing_record
-- ----------------------------
DROP TABLE IF EXISTS `t_pricing_record`;
CREATE TABLE `t_pricing_record`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                     `policy_holder_id` bigint NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                     `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全链路追踪ID',
                                     `risk_decision_id` bigint NOT NULL COMMENT '关联风控决策记录ID（t_risk_decision_record.id）',
                                     `base_premium` decimal(20, 6) NOT NULL COMMENT '基础保费',
                                     `final_premium` decimal(20, 6) NOT NULL COMMENT '最终保费',
                                     `premium_start_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '保费生效时间',
                                     `premium_end_time` datetime NOT NULL COMMENT '保费失效时间',
                                     `pricing_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '定价时间',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_policy_holder_id`(`policy_holder_id` ASC) USING BTREE COMMENT '投保人ID索引',
                                     INDEX `idx_pricing_time`(`pricing_time` ASC) USING BTREE COMMENT '定价时间索引',
                                     INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE COMMENT 'trace_id索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '保费定价记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_pricing_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_risk_decision_record
-- ----------------------------
DROP TABLE IF EXISTS `t_risk_decision_record`;
CREATE TABLE `t_risk_decision_record`  (
                                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                           `policy_holder_id` bigint NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                           `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全链路追踪ID',
                                           `anti_record_id` bigint NOT NULL COMMENT '关联逆选择记录ID（t_antiselection_record.id）',
                                           `data_model_prob` decimal(7, 6) NOT NULL COMMENT '数据模型风险概率（0-1）',
                                           `agent_prob` decimal(7, 6) NULL DEFAULT NULL COMMENT '智能体（豆包）风险概率（0-1）',
                                           `final_risk_prob` decimal(7, 6) NOT NULL COMMENT '最终风险概率（0-1）',
                                           `risk_decision` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风控决策（承保/拒保）',
                                           `decision_reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '决策原因',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `idx_policy_holder_id`(`policy_holder_id` ASC) USING BTREE COMMENT '投保人ID索引',
                                           INDEX `idx_create_time`(`create_time` ASC) USING BTREE COMMENT '创建时间索引',
                                           INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE COMMENT 'trace_id索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '风控决策记录表（金融审计核心）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_risk_decision_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_risk_factor_record
-- ----------------------------
DROP TABLE IF EXISTS `t_risk_factor_record`;
CREATE TABLE `t_risk_factor_record`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `policy_holder_id` bigint NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                         `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全链路追踪ID（单次核保请求唯一标识）',
                                         `age_risk_value` decimal(7, 6) NOT NULL COMMENT '年龄风险因子（0-1）',
                                         `occupation_risk_value` decimal(7, 6) NOT NULL COMMENT '职业风险因子（0-1）',
                                         `health_risk_value` decimal(7, 6) NOT NULL COMMENT '健康风险因子（0-1）',
                                         `amount_risk_value` decimal(7, 6) NOT NULL COMMENT '保额风险因子（0-1）',
                                         `total_risk_value` decimal(7, 6) NOT NULL COMMENT '总风险因子值（0-1）',
                                         `calculate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
                                         `calculator_user` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '计算人（系统/人工）',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `idx_policy_holder_id`(`policy_holder_id` ASC) USING BTREE COMMENT '投保人ID索引',
                                         INDEX `idx_calculate_time`(`calculate_time` ASC) USING BTREE COMMENT '计算时间索引',
                                         INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE COMMENT 'trace_id索引（快速查询单次流程）'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '风险因子计算记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_risk_factor_record
-- ----------------------------

-- ----------------------------
-- Table structure for t_sum_insured_risk_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_sum_insured_risk_dict`;
CREATE TABLE `t_sum_insured_risk_dict`  (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                            `sum_insured_min` decimal(16, 2) NOT NULL COMMENT '保额下限（含，单位：元）',
                                            `sum_insured_max` decimal(16, 2) NOT NULL COMMENT '保额上限（含，单位：元）',
                                            `risk_value` decimal(3, 2) NOT NULL COMMENT '保额风险值（0-1）',
                                            `risk_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风险等级（低/较低/中/高/极高风险）',
                                            `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用（1=是，0=否）',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（自动刷新）',
                                            PRIMARY KEY (`id`) USING BTREE,
                                            UNIQUE INDEX `uk_sum_insured_range`(`sum_insured_min` ASC, `sum_insured_max` ASC) USING BTREE COMMENT '保额区间唯一索引',
                                            CONSTRAINT `ck_sum_insured_range` CHECK (`sum_insured_min` <= `sum_insured_max`)
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '保额风险字典表（校验规则：保额下限≤上限）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_sum_insured_risk_dict
-- ----------------------------
INSERT INTO `t_sum_insured_risk_dict` VALUES (6, 0.00, 100000.00, 0.10, '低风险', 1, '2026-03-19 15:48:05', '2026-03-19 15:48:05');
INSERT INTO `t_sum_insured_risk_dict` VALUES (7, 100000.01, 500000.00, 0.30, '较低风险', 1, '2026-03-19 15:48:05', '2026-03-19 15:48:05');
INSERT INTO `t_sum_insured_risk_dict` VALUES (8, 500000.01, 1000000.00, 0.50, '中风险', 1, '2026-03-19 15:48:05', '2026-03-19 15:48:05');
INSERT INTO `t_sum_insured_risk_dict` VALUES (9, 1000000.01, 2000000.00, 0.70, '高风险', 1, '2026-03-19 15:48:05', '2026-03-19 15:48:05');
INSERT INTO `t_sum_insured_risk_dict` VALUES (10, 2000000.01, 999999999.99, 0.90, '极高风险', 1, '2026-03-19 15:48:05', '2026-03-19 15:48:05');

-- ----------------------------
-- Table structure for t_underwriting_record
-- ----------------------------
DROP TABLE IF EXISTS `t_underwriting_record`;
CREATE TABLE `t_underwriting_record`  (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                          `policy_holder_id` bigint NOT NULL COMMENT '投保人ID（关联t_policy_holder.id）',
                                          `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '全链路追踪ID',
                                          `risk_decision_id` bigint NOT NULL COMMENT '关联风控决策记录ID（t_risk_decision_record.id）',
                                          `pricing_record_id` bigint NOT NULL COMMENT '关联保费定价记录ID（t_pricing_record.id）',
                                          `underwriting_result` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '承保结果（承保/拒保/加费）',
                                          `underwriting_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '承保时间',
                                          `operator` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'system' COMMENT '操作人员（系统/人工核保员）',
                                          PRIMARY KEY (`id`) USING BTREE,
                                          INDEX `idx_policy_holder_id`(`policy_holder_id` ASC) USING BTREE COMMENT '投保人ID索引',
                                          INDEX `idx_underwriting_time`(`underwriting_time` ASC) USING BTREE COMMENT '承保时间索引',
                                          INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE COMMENT 'trace_id索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '承保记录总表（最终归档）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_underwriting_record
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
