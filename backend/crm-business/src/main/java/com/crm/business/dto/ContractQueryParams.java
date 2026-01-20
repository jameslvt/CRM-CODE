package com.crm.business.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 合同查询参数DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ContractQueryParams {

    /**
     * 合同编号（模糊查询）
     */
    private String contractNo;

    /**
     * 合同名称（模糊查询）
     */
    private String name;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称（模糊查询）
     */
    private String customerName;

    /**
     * 商机ID
     */
    private Long opportunityId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 签约日期开始
     */
    private LocalDate signDateStart;

    /**
     * 签约日期结束
     */
    private LocalDate signDateEnd;

    /**
     * 创建时间开始
     */
    private LocalDate createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDate createTimeEnd;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
