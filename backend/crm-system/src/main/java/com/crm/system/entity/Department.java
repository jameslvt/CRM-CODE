package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体类
 * 对应数据库表 crm_department
 * 支持树形结构
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_department")
public class Department extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父部门 ID（0 表示顶级部门）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门编码（唯一标识）
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 部门负责人 ID
     */
    @TableField("leader_id")
    private Long leaderId;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

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
