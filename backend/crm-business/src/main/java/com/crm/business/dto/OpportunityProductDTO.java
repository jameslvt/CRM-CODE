package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商机产品DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class OpportunityProductDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 商机ID
     */
    private Long opportunityId;

    /**
     * 产品ID
     */
    @NotNull(message = "产品不能为空")
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0", message = "单价不能为负数")
    private BigDecimal price;

    /**
     * 折扣 (%)
     */
    @DecimalMin(value = "0", message = "折扣不能为负数")
    private BigDecimal discount;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 计量单位
     */
    private String unit;
}
