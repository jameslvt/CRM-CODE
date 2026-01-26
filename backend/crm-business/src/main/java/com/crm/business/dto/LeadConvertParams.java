package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 线索转化参数
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
public class LeadConvertParams {

    /**
     * 线索ID
     */
    @NotNull(message = "线索ID不能为空")
    private Long leadId;

    /**
     * 客户名称
     */
    @NotNull(message = "客户名称不能为空")
    private String customerName;

    /**
     * 客户类型
     */
    private String customerType;

    /**
     * 客户级别
     */
    private String customerLevel;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 行业
     */
    private String industry;

    /**
     * 规模
     */
    private String scale;

    /**
     * 来源
     */
    private String source;

    /**
     * 客户联系电话
     */
    private String customerPhone;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 联系人职位
     */
    private String contactPosition;

    /**
     * 联系人性别
     */
    private Integer contactGender;

    /**
     * 是否创建商机
     */
    private Boolean createOpportunity = false;

    /**
     * 商机名称（如果创建商机）
     */
    private String opportunityName;

    /**
     * 商机金额（如果创建商机）
     */
    private Long opportunityAmount;

    /**
     * 预计成交日期（如果创建商机）时间戳
     */
    private Long expectedCloseDate;

    /**
     * 商机阶段（如果创建商机）
     */
    private Integer opportunityStage;

    /**
     * 备注
     */
    private String remark;
}
