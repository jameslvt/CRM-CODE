package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 * 对应数据库表 crm_permission
 * 支持树形结构（目录、菜单、按钮权限）
 * 注意：此表没有 deleted 字段，不继承 BaseEntity
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@TableName("crm_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 父权限 ID（0 表示顶级权限）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 权限名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限标识（唯一，如 system:user:add）
     */
    @TableField("permission_key")
    private String permissionKey;

    /**
     * 类型: 1-目录, 2-菜单, 3-按钮
     */
    @TableField("type")
    private Integer type;

    /**
     * 路由路径（前端路由）
     */
    @TableField("path")
    private String path;

    /**
     * 组件路径（前端组件）
     */
    @TableField("component")
    private String component;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 排序号（数字越小越靠前）
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否可见: 0-隐藏, 1-显示
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 状态（0: 禁用, 1: 正常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
