package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 * 对应数据库表 crm_permission
 * 支持树形结构（菜单、按钮权限）
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父权限 ID（0 表示顶级权限）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 权限编码（唯一标识，如 system:user:add）
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限类型（1: 菜单, 2: 按钮, 3: 接口）
     */
    @TableField("permission_type")
    private Integer permissionType;

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
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 是否可见（0: 隐藏, 1: 显示）
     */
    @TableField("visible")
    private Integer visible;

    /**
     * 状态（0: 禁用, 1: 启用）
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
