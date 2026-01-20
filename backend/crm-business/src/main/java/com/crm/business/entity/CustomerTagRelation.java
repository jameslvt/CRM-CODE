package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 客户标签关联实体
 * 对应数据库表 crm_customer_tag_relation
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@TableName("crm_customer_tag_relation")
public class CustomerTagRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;
}
