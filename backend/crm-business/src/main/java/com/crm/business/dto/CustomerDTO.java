package com.crm.business.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户DTO
 * 用于客户信息的传输
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class CustomerDTO {

    /**
     * 客户ID
     */
    private Long id;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 客户编码
     */
    private String code;

    /**
     * 行业
     */
    private String industry;

    /**
     * 规模: 小型、中型、大型、集团
     */
    private String scale;

    /**
     * 来源
     */
    private String source;

    /**
     * 级别: A、B、C、D
     */
    private String level;

    /**
     * 电话
     */
    private String phone;

    /**
     * 传真
     */
    private String fax;

    /**
     * 网站
     */
    private String website;

    /**
     * 地址
     */
    private String address;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人姓名（关联查询）
     */
    private String ownerName;

    /**
     * 来源线索ID
     */
    private Long leadId;

    /**
     * 状态: 1-正常, 2-公海
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
