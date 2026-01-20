package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商机阶段配置实体
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_opportunity_stage")
public class OpportunityStage extends BaseEntity {

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 阶段编码（唯一标识）
     */
    private String stageCode;

    /**
     * 赢单概率（0-100）
     */
    private BigDecimal winRate;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态：0-停用，1-正常
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
