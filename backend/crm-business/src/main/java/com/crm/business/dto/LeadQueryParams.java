package com.crm.business.dto;

import lombok.Data;

/**
 * 线索查询参数
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class LeadQueryParams {

    /**
     * 关键词（线索名称、公司名称、电话）
     */
    private String keyword;

    /**
     * 来源
     */
    private String source;

    /**
     * 状态: 1-新建, 2-跟进中, 3-已转化, 4-已关闭
     */
    private Integer status;

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
