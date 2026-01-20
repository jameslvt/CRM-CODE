package com.crm.business.dto;

import lombok.Data;

/**
 * 客户查询参数DTO
 * 用于客户列表查询的参数封装
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class CustomerQueryParams {

    /**
     * 客户名称（模糊查询）
     */
    private String name;

    /**
     * 行业
     */
    private String industry;

    /**
     * 级别: A、B、C、D
     */
    private String level;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 状态: 1-正常, 2-公海
     */
    private Integer status;

    /**
     * 是否查询公海客户
     */
    private Boolean isPublicPool;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}
