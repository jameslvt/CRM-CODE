package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 跟进记录DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ActivityDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 类型: 电话、拜访、邮件、会议、其他
     */
    @NotBlank(message = "跟进类型不能为空")
    private String type;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 跟进内容
     */
    @NotBlank(message = "跟进内容不能为空")
    @Size(min = 1, max = 2000, message = "跟进内容长度不能超过2000个字符")
    private String content;

    /**
     * 关联对象类型: lead、customer、opportunity
     */
    @NotBlank(message = "关联对象类型不能为空")
    private String targetType;

    /**
     * 关联对象类型名称
     */
    private String targetTypeName;

    /**
     * 关联对象 ID
     */
    @NotNull(message = "关联对象不能为空")
    private Long targetId;

    /**
     * 关联对象名称
     */
    private String targetName;

    /**
     * 下次跟进时间
     */
    private LocalDateTime nextTime;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建人名称
     */
    private String createByName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
