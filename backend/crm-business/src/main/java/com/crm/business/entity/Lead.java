package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线索实体
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_lead")
public class Lead extends BaseEntity {

    /**
     * 线索名称
     */
    private String name;

    /**
     * 联系人姓名
     */
    private String contactName;

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
     * 线索来源：1-网站，2-电话，3-推荐，4-展会，5-其他
     */
    private Integer source;

    /**
     * 线索状态：1-新建，2-跟进中，3-已转化，4-已失效
     */
    private Integer status;

    /**
     * 线索评级：1-A（高），2-B（中），3-C（低）
     */
    private Integer rating;

    /**
     * 预计金额（元）
     */
    private Long estimatedAmount;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人姓名
     */
    private String ownerName;

    /**
     * 地址
     */
    private String address;

    /**
     * 行业
     */
    private String industry;

    /**
     * 备注
     */
    private String remark;

    /**
     * 转化时间
     */
    private String convertTime;

    /**
     * 转化后的客户ID
     */
    private Long customerId;
}
