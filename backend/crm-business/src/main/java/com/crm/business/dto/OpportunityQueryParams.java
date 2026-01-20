package com.crm.business.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 商机查询参数DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class OpportunityQueryParams {

    /**
     * 商机名称（模糊查询）
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
     * 阶段
     */
    private String stage;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 商机来源
     */
    private String source;

    /**
     * 预计成交日期开始
     */
    private LocalDate expectedDateStart;

    /**
     * 预计成交日期结束
     */
    private LocalDate expectedDateEnd;

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
