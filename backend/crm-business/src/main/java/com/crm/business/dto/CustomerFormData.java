package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 客户表单数据DTO
 * 用于创建和更新客户时的数据传输
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class CustomerFormData {

    /**
     * 客户ID（更新时使用）
     */
    private Long id;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    @Size(min = 2, max = 200, message = "客户名称长度必须在2-200个字符之间")
    private String name;

    /**
     * 简称
     */
    @Size(max = 50, message = "简称长度不能超过50个字符")
    private String shortName;

    /**
     * 行业
     */
    @Size(max = 50, message = "行业长度不能超过50个字符")
    private String industry;

    /**
     * 规模: 小型、中型、大型、集团
     */
    @Size(max = 50, message = "规模长度不能超过50个字符")
    private String scale;

    /**
     * 来源
     */
    @Size(max = 50, message = "来源长度不能超过50个字符")
    private String source;

    /**
     * 级别: A、B、C、D
     */
    @Size(max = 20, message = "级别长度不能超过20个字符")
    private String level;

    /**
     * 电话
     */
    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;

    /**
     * 传真
     */
    @Size(max = 20, message = "传真长度不能超过20个字符")
    private String fax;

    /**
     * 网站
     */
    @Size(max = 200, message = "网站长度不能超过200个字符")
    private String website;

    /**
     * 地址
     */
    @Size(max = 500, message = "地址长度不能超过500个字符")
    private String address;

    /**
     * 备注
     */
    private String remark;
}
