package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 联系人实体
 * 对应数据库表 crm_contact
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_contact")
public class Contact extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 客户 ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 性别: 1-男, 2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 部门
     */
    @TableField("department")
    private String department;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 手机
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 微信
     */
    @TableField("wechat")
    private String wechat;

    /**
     * 是否主要联系人: 0-否, 1-是
     */
    @TableField("is_primary")
    private Integer isPrimary;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDate birthday;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
