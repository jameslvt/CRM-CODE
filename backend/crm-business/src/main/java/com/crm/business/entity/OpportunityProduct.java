package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商机产品关联实体
 * 对应数据库表 crm_opportunity_product
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_opportunity_product")
public class OpportunityProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商机 ID
     */
    @TableField("opportunity_id")
    private Long opportunityId;

    /**
     * 产品 ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 折扣 (%)
     */
    @TableField("discount")
    private BigDecimal discount;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;
}
