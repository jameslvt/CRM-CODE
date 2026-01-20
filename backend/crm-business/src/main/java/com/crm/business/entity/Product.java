package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 产品实体
 * 对应数据库表 crm_product
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 产品名称
     */
    @TableField("name")
    private String name;

    /**
     * 产品编码
     */
    @TableField("code")
    private String code;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 计量单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 单价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 状态: 0-停用, 1-启用
     */
    @TableField("status")
    private Integer status;
}
