package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 * 对应数据库表 crm_user
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（登录账号，唯一）
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 手机号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 性别（0: 未知, 1: 男, 2: 女）
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 头像 URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 部门 ID
     */
    @TableField("department_id")
    private Long departmentId;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

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

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private java.time.LocalDateTime lastLoginTime;

    /**
     * 最后登录 IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;
}
