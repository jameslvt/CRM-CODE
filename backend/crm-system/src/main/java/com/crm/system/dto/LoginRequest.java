package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求 DTO
 * 用于接收用户登录请求参数
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "登录请求对象", description = "用户登录时提交的数据")
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码（可选）
     */
    @ApiModelProperty(value = "验证码", example = "1234")
    private String captcha;

    /**
     * 验证码 Key（可选）
     */
    @ApiModelProperty(value = "验证码Key", example = "uuid-key")
    private String captchaKey;
}
