package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_dict_data")
public class DictData extends BaseEntity {

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典键值
     */
    private String dictValue;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 样式属性（如颜色、图标等）
     */
    private String cssClass;

    /**
     * 表格回显样式
     */
    private String listClass;

    /**
     * 是否默认：0-否，1-是
     */
    private Integer isDefault;

    /**
     * 状态：0-停用，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
