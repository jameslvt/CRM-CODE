package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 商机实体
 * 对应数据库表 crm_opportunity
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_opportunity")
public class Opportunity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商机名称
     */
    @TableField("name")
    private String name;

    /**
     * 客户 ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 联系人 ID
     */
    @TableField("contact_id")
    private Long contactId;

    /**
     * 预计金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 阶段: 需求确认、方案报价、商务谈判、赢单、输单
     */
    @TableField("stage")
    private String stage;

    /**
     * 赢单概率 (%)
     */
    @TableField("probability")
    private Integer probability;

    /**
     * 预计成交日期
     */
    @TableField("expected_date")
    private LocalDate expectedDate;

    /**
     * 负责人 ID
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 商机来源
     */
    @TableField("source")
    private String source;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 关闭原因 (输单时)
     */
    @TableField("close_reason")
    private String closeReason;

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
