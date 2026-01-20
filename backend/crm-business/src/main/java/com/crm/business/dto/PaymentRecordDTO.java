package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 回款记录DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class PaymentRecordDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 回款计划ID
     */
    @NotNull(message = "回款计划不能为空")
    private Long planId;

    /**
     * 回款金额
     */
    @NotNull(message = "回款金额不能为空")
    @DecimalMin(value = "0.01", message = "回款金额必须大于0")
    private BigDecimal amount;

    /**
     * 回款日期
     */
    @NotNull(message = "回款日期不能为空")
    private LocalDate paymentDate;

    /**
     * 付款方式: 银行转账、支票、现金
     */
    private String paymentMethod;

    /**
     * 付款方式名称
     */
    private String paymentMethodName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createByName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
