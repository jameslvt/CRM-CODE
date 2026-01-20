package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_dict_type")
public class DictType extends BaseEntity {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型（唯一标识）
     */
    private String dictType;

    /**
     * 状态：0-停用，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
