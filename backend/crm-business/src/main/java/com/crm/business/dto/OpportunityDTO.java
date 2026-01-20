package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商机DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class OpportunityDTO {

    /**
     * 商机ID
     */
    private Long id;

    /**
     * 商机名称
     */
    @NotBlank(message = "商机名称不能为空")
    @Size(min = 2, max = 200, message = "商机名称长度必须在2-200个字符之间")
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
     * 联系人ID
     */
    private Long contactId;

    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 预计金额
     */
    private BigDecimal amount;

    /**
     * 阶段: 需求确认、方案报价、商务谈判、赢单、输单
     */
    @NotBlank(message = "阶段不能为空")
    private String stage;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 赢单概率 (%)
     */
    private Integer probability;

    /**
     * 预计成交日期
     */
    private LocalDate expectedDate;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 负责人名称
     */
    private String ownerName;

    /**
     * 商机来源
     */
    private String source;

    /**
     * 来源名称
     */
    private String sourceName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关闭原因 (输单时)
     */
    private String closeReason;

    /**
     * 商机产品列表
     */
    private List<OpportunityProductDTO> products;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
