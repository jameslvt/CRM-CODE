package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商机产品关联实体
 * 对应数据库表 crm_opportunity_product
 * 
 * 注意：此表为关联表，不继承 BaseEntity，因为不需要时间戳和审计字段
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@TableName("crm_opportunity_product")
public class OpportunityProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

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
