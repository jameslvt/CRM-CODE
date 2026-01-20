package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录响应 DTO
 * 用于返回登录成功后的用户信息和 Token
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "登录响应对象", description = "登录成功后返回的数据")
public class LoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @ApiModelProperty(value = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @ApiModelProperty(value = "刷新令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * 令牌类型
     */
    @ApiModelProperty(value = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    /**
     * 过期时间（秒）
     */
    @ApiModelProperty(value = "过期时间（秒）", example = "7200")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @ApiModelProperty(value = "用户信息")
    private UserDTO userInfo;

    /**
     * 用户权限列表
     */
    @ApiModelProperty(value = "用户权限列表")
    private List<String> permissions;

    /**
     * 用户角色列表
     */
    @ApiModelProperty(value = "用户角色列表")
    private List<String> roles;
}
