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
}
