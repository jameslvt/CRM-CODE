package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户标签实体
 * 对应数据库表 crm_customer_tag
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer_tag")
public class CustomerTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 标签颜色（十六进制颜色值）
     */
    @TableField("color")
    private String color;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
