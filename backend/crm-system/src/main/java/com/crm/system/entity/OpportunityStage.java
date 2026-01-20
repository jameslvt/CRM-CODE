package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商机阶段配置实体
 * 对应数据库表 crm_opportunity_stage
 * 注意：此表没有 deleted 字段，不继承 BaseEntity
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@TableName("crm_opportunity_stage")
public class OpportunityStage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId("id")
    private Long id;

    /**
     * 阶段名称
     */
    @TableField("name")
    private String name;

    /**
     * 阶段标识（唯一）
     */
    @TableField("stage_key")
    private String stageKey;

    /**
     * 默认赢单概率 (%)
     */
    @TableField("probability")
    private Integer probability;

    /**
     * 排序号
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 状态：0-禁用，1-正常
     */
    @TableField("status")
    private Integer status;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
