package com.crm.business.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crm.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 跟进记录实体
 * 对应数据库表 crm_activity
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_activity")
public class Activity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 类型: 电话、拜访、邮件、会议、其他
     */
    @TableField("type")
    private String type;

    /**
     * 跟进内容
     */
    @TableField("content")
    private String content;

    /**
     * 关联对象类型: lead、customer、opportunity
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 关联对象 ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 下次跟进时间
     */
    @TableField("next_time")
    private LocalDateTime nextTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    private Long createBy;
}
