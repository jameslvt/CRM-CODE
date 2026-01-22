package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 线索实体
 * 对应数据库表 crm_lead
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_lead")
public class Lead extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 线索名称
     */
    @TableField("name")
    private String name;

    /**
     * 来源: 官网、广告、转介绍等
     */
    @TableField("source")
    private String source;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 公司名称
     */
    @TableField("company")
    private String company;

    /**
     * 职位
     */
    @TableField("position")
    private String position;

    /**
     * 行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 评级: A-高, B-中, C-低, D-极低
     */
    @TableField("rating")
    private String rating;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 状态: 1-新建, 2-跟进中, 3-已转化, 4-已关闭
     */
    @TableField("status")
    private Integer status;

    /**
     * 负责人 ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 转化后的客户 ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    private Long updateBy;
}
