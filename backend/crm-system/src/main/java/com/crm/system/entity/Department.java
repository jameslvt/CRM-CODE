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
     * 祖先路径（逗号分隔，如 "0,1,2"）
     */
    @TableField("ancestors")
    private String ancestors;

    /**
     * 部门编码（唯一）
     */
    @TableField("code")
    private String code;

    /**
     * 部门名称
     */
    @TableField("name")
    private String name;

    /**
     * 部门负责人 ID
     */
    @TableField("leader_id")
    private Long leaderId;

    /**
     * 排序号（数字越小越靠前）
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态（0: 禁用, 1: 正常）
     */
    @TableField("status")
    private Integer status;
}
