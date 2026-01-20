package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 回款计划DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class PaymentPlanDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 合同ID
     */
    @NotNull(message = "合同不能为空")
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 期数
     */
    @NotNull(message = "期数不能为空")
    @Min(value = 1, message = "期数必须大于0")
    private Integer period;

    /**
     * 计划金额
     */
    @NotNull(message = "计划金额不能为空")
    @DecimalMin(value = "0.01", message = "计划金额必须大于0")
    private BigDecimal planAmount;

    /**
     * 计划回款日期
     */
    @NotNull(message = "计划回款日期不能为空")
    private LocalDate planDate;

    /**
     * 实际回款金额
     */
    private BigDecimal actualAmount;

    /**
     * 实际回款日期
     */
    private LocalDate actualDate;

    /**
     * 状态: 1-待回款, 2-部分回款, 3-已回款, 4-逾期
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
     * 回款记录列表
     */
    private List<PaymentRecordDTO> records;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
