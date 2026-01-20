package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户 DTO
 * 用于用户数据的传输和展示
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "用户数据传输对象", description = "用户信息")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码（仅用于创建和修改，不返回）
     */
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名", required = true, example = "张三")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", example = "admin@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 性别（0: 未知, 1: 男, 2: 女）
     */
    @ApiModelProperty(value = "性别（0:未知,1:男,2:女）", example = "1")
    private Integer gender;

    /**
     * 头像 URL
     */
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 部门 ID
     */
    @ApiModelProperty(value = "部门ID", example = "1")
    private Long departmentId;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称", example = "技术部")
    private String departmentName;

    /**
     * 职位
     */
    @ApiModelProperty(value = "职位", example = "Java开发工程师")
    private String position;

    /**
     * 状态（0: 禁用, 1: 启用）
     */
    @ApiModelProperty(value = "状态（0:禁用,1:启用）", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "系统管理员")
    private String remark;

    /**
     * 最后登录时间
     */
    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录 IP
     */
    @ApiModelProperty(value = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
