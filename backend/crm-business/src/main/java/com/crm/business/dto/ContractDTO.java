package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ContractDTO {

    /**
     * 合同ID
     */
    private Long id;

    /**
     * 合同编号
     */
    @Size(max = 50, message = "合同编号长度不能超过50个字符")
    private String contractNo;

    /**
     * 合同名称
     */
    @NotBlank(message = "合同名称不能为空")
    @Size(min = 2, max = 200, message = "合同名称长度必须在2-200个字符之间")
    private String name;

    /**
     * 客户ID
     */
    @NotNull(message = "客户不能为空")
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 商机ID
     */
    private Long opportunityId;

    /**
     * 商机名称
     */
    private String opportunityName;

    /**
     * 合同金额
     */
    @NotNull(message = "合同金额不能为空")
    @DecimalMin(value = "0", message = "合同金额不能为负数")
    private BigDecimal amount;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 签约日期
     */
    private LocalDate signDate;

    /**
     * 状态: 1-草稿, 2-审批中, 3-执行中, 4-已完成, 5-已终止
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
     * 负责人名称
     */
    private String ownerName;

    /**
     * 合同文件 URL
     */
    private String fileUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 已回款金额
     */
    private BigDecimal paidAmount;

    /**
     * 未回款金额
     */
    private BigDecimal unpaidAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
