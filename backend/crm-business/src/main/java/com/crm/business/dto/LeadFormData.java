package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 线索表单数据
 * 对应数据库表 crm_lead
 *
 * @author CRM System
 * @since 1.0.0
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
     * 客户名称
     */
    private String customerName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 来源: 官网、广告、转介绍等
     */
    private String source;

    /**
     * 联系电话
     */
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
     * 行业
     */
    private String industry;

    /**
     * 评级: A-高, B-中, C-低, D-极低
     */
    private String rating;

    /**
     * 地址
     */
    private String address;

    /**
     * 状态: 1-新建, 2-跟进中, 3-已转化, 4-已关闭
     */
    private Integer status;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 备注
     */
    private String remark;
}
