package com.crm.business.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 线索DTO
 * 对应数据库表 crm_lead
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class LeadDTO {

    /**
     * 线索ID
     */
    private Long id;

    /**
     * 线索名称
     */
    private String name;

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
     * 状态名称
     */
    private String statusName;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人姓名（关联查询）
     */
    private String ownerName;

    /**
     * 转化后的客户ID
     */
    private Long customerId;

    /**
     * 转化时间
     */
    private LocalDateTime convertTime;

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
