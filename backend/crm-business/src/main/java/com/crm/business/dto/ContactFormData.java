package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 联系人表单数据DTO
 * 用于创建和更新联系人时的数据传输
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ContactFormData {

    /**
     * 联系人ID（更新时使用）
     */
    private Long id;

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度必须在2-50个字符之间")
    private String name;

    /**
     * 性别: 1-男, 2-女
     */
    private Integer gender;

    /**
     * 职位
     */
    @Size(max = 50, message = "职位长度不能超过50个字符")
    private String position;

    /**
     * 部门
     */
    @Size(max = 50, message = "部门长度不能超过50个字符")
    private String department;

    /**
     * 电话
     */
    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;

    /**
     * 手机
     */
    @Size(max = 20, message = "手机长度不能超过20个字符")
    private String mobile;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 微信
     */
    @Size(max = 50, message = "微信长度不能超过50个字符")
    private String wechat;

    /**
     * 是否主要联系人
     */
    private Boolean isPrimary;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 备注
     */
    private String remark;
}
