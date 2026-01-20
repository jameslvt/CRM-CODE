package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 阶段推进DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class StageAdvanceDTO {

    /**
     * 商机ID
     */
    @NotNull(message = "商机ID不能为空")
    private Long opportunityId;

    /**
     * 目标阶段
     */
    @NotBlank(message = "目标阶段不能为空")
    private String targetStage;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关闭原因（输单时必填）
     */
    private String closeReason;
}
