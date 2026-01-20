package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 线索表单数据
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
public class LeadFormData {

    /**
     * 线索ID（编辑时必填）
     */
    private Long id;

    /**
     * 线索名称
     */
    @NotBlank(message = "线索名称不能为空")
    private String name;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 公司名称
     */
    private String company;

    /**
     * 职位
     */
    private String position;

    /**
     * 线索来源
     */
    @NotNull(message = "线索来源不能为空")
    private Integer source;

    /**
     * 线索状态
     */
    @NotNull(message = "线索状态不能为空")
    private Integer status;

    /**
     * 线索评级
     */
    private Integer rating;

    /**
     * 预计金额（元）
     */
    private Long estimatedAmount;

    /**
     * 负责人ID
     */
    @NotNull(message = "负责人不能为空")
    private Long ownerId;

    /**
     * 地址
     */
    private String address;

    /**
     * 行业
     */
    private String industry;

    /**
     * 备注
     */
    private String remark;
}
