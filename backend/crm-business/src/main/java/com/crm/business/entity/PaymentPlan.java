package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 回款计划实体
 * 对应数据库表 crm_payment_plan
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_payment_plan")
public class PaymentPlan extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 合同 ID
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 期数
     */
    @TableField("period")
    private Integer period;

    /**
     * 计划金额
     */
    @TableField("plan_amount")
    private BigDecimal planAmount;

    /**
     * 计划回款日期
     */
    @TableField("plan_date")
    private LocalDate planDate;

    /**
     * 实际回款金额
     */
    @TableField("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 实际回款日期
     */
    @TableField("actual_date")
    private LocalDate actualDate;

    /**
     * 状态: 1-待回款, 2-部分回款, 3-已回款, 4-逾期
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
