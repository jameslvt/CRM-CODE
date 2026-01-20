package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 * 对应数据库表 crm_role
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_role")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色编码（唯一标识，如 ROLE_ADMIN）
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色名称
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    /**
     * 排序号（数字越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

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
