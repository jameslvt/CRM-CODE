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
     * 密码（BCrypt 加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 头像 URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 所属部门 ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 状态（0: 禁用, 1: 正常）
     */
    @TableField("status")
    private Integer status;
}
