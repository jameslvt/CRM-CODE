package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 回款记录实体
 * 对应数据库表 crm_payment_record
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_payment_record")
public class PaymentRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 回款计划 ID
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 回款金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 回款日期
     */
    @TableField("payment_date")
    private LocalDate paymentDate;

    /**
     * 付款方式: 银行转账、支票、现金
     */
    @TableField("payment_method")
    private String paymentMethod;

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
}
