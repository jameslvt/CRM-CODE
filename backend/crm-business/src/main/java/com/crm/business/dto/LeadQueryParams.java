package com.crm.business.dto;

import lombok.Data;

/**
 * 线索查询参数
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
public class LeadQueryParams {

    /**
     * 关键词（线索名称、联系人、公司名称）
     */
    private String keyword;

    /**
     * 线索来源
     */
    private Integer source;

    /**
     * 线索状态
     */
    private Integer status;

    /**
     * 线索评级
     */
    private Integer rating;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 行业
     */
    private String industry;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
