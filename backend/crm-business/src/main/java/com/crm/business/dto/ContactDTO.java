package com.crm.business.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 联系人DTO
 * 用于联系人信息的传输
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ContactDTO {

    /**
     * 联系人ID
     */
    private Long id;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称（关联查询）
     */
    private String customerName;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别: 1-男, 2-女
     */
    private Integer gender;

    /**
     * 性别名称
     */
    private String genderName;

    /**
     * 职位
     */
    private String position;

    /**
     * 部门
     */
    private String department;

    /**
     * 电话
     */
    private String phone;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 是否主要联系人: 0-否, 1-是
     */
    private Integer isPrimary;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
