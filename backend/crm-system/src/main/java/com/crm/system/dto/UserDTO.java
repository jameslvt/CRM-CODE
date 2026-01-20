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
     * 昵称
     */
    @ApiModelProperty(value = "昵称", example = "管理员")
    private String nickname;

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
     * 头像 URL
     */
    @ApiModelProperty(value = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 部门 ID
     */
    @ApiModelProperty(value = "部门ID", example = "1")
    private Long deptId;

    /**
     * 部门名称（仅用于展示）
     */
    @ApiModelProperty(value = "部门名称", example = "技术部")
    private String deptName;

    /**
     * 状态（0: 禁用, 1: 正常）
     */
    @ApiModelProperty(value = "状态（0:禁用,1:正常）", example = "1")
    private Integer status;

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
