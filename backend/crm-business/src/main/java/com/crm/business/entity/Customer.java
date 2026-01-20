package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户实体
 * 对应数据库表 crm_customer
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer")
public class Customer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 客户名称
     */
    @TableField("name")
    private String name;

    /**
     * 简称
     */
    @TableField("short_name")
    private String shortName;

    /**
     * 客户编码
     */
    @TableField("code")
    private String code;

    /**
     * 行业
     */
    @TableField("industry")
    private String industry;

    /**
     * 规模: 小型、中型、大型、集团
     */
    @TableField("scale")
    private String scale;

    /**
     * 来源
     */
    @TableField("source")
    private String source;

    /**
     * 级别: A、B、C、D
     */
    @TableField("level")
    private String level;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 传真
     */
    @TableField("fax")
    private String fax;

    /**
     * 网站
     */
    @TableField("website")
    private String website;

    /**
     * 地址
     */
    @TableField("address")
    private String address;

    /**
     * 负责人 ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 来源线索 ID
     */
    @TableField("lead_id")
    private Long leadId;

    /**
     * 状态: 1-正常, 2-公海
     */
    @TableField("status")
    private Integer status;

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
