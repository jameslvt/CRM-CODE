package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色 DTO
 * 用于角色数据的传输和展示
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "角色数据传输对象", description = "角色信息")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色 ID
     */
    @ApiModelProperty(value = "角色ID", example = "1")
    private Long id;

    /**
     * 角色编码
     */
    @ApiModelProperty(value = "角色编码", required = true, example = "ROLE_ADMIN")
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", required = true, example = "系统管理员")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", example = "拥有系统所有权限")
    private String description;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", example = "1")
    private Integer sortOrder;

    /**
     * 状态（0: 禁用, 1: 启用）
     */
    @ApiModelProperty(value = "状态（0:禁用,1:启用）", example = "1")
    private Integer status;

    /**
     * 数据权限范围: 1-全部数据, 2-本部门及下级, 3-本部门, 4-仅本人
     */
    @ApiModelProperty(value = "数据权限范围", example = "1")
    private Integer dataScope;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "系统内置角色")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
