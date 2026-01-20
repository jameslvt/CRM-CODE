package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体类
 * 对应数据库表 crm_role_permission
 * 用于维护角色和权限的多对多关系
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_role_permission")
public class RolePermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色 ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 权限 ID
     */
    @TableField("permission_id")
    private Long permissionId;
}
