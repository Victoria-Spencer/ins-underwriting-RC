package org.allen.ins.underwriting.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 * 功能：自动填充所有实体类中标记了 FieldFill.INSERT/UPDATE 的字段
 * 支持：createTime（插入时填充）、updateTime（插入/更新时填充）
 * 扩展：可按需添加 createUser、updateUser 等字段的填充逻辑
 */
@Component // 必须加！Spring容器扫描并管理该类，否则填充逻辑不生效
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时的字段填充（对应 @TableField(fill = FieldFill.INSERT)）
     * @param metaObject 元对象（封装了实体类的字段和值）
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // ========== 1. 填充创建时间（createTime） ==========
        // strictInsertFill：严格模式，仅当字段为null且标记了FieldFill.INSERT时才填充
        this.strictInsertFill(
                metaObject,          // 元对象（固定传）
                "createTime",        // 实体类的驼峰字段名（必须和实体类一致，如createTime对应数据库create_time）
                LocalDateTime.class, // 字段类型（必须和实体类字段类型一致）
                LocalDateTime.now()  // 填充值（当前系统时间，东八区）
        );

        // ========== 2. 填充更新时间（updateTime）（插入时也填充） ==========
        this.strictInsertFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

        // ========= 3. 计算时间（calculateTime）=========
        this.strictInsertFill(
                metaObject,
                "calculateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

        // ========= 4. 定价时间（calculateTime）=========
        this.strictInsertFill(
                metaObject,
                "pricingTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

        // ========== 扩展：填充创建人（createUser）（如有需要，取消注释并适配） ==========
        // 示例：从上下文获取当前登录用户ID
        // Long currentUserId = SecurityUtils.getCurrentUserId();
        // this.strictInsertFill(metaObject, "createUser", Long.class, currentUserId);
    }

    /**
     * 更新操作时的字段填充（对应 @TableField(fill = FieldFill.UPDATE) 或 FieldFill.INSERT_UPDATE）
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // ========== 1. 填充更新时间（updateTime） ==========
        this.strictUpdateFill(
                metaObject,
                "updateTime",
                LocalDateTime.class,
                LocalDateTime.now()
        );

        // ========== 扩展：填充更新人（updateUser）（如有需要，取消注释并适配） ==========
        // Long currentUserId = SecurityUtils.getCurrentUserId();
        // this.strictUpdateFill(metaObject, "updateUser", Long.class, currentUserId);
    }
}