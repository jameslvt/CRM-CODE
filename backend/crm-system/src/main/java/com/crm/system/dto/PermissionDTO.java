package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限 DTO
 * 用于权限数据的传输和展示
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "权限数据传输对象", description = "权限信息")
public class PermissionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限 ID
     */
    @ApiModelProperty(value = "权限ID", example = "1")
    private Long id;

    /**
     * 父权限 ID
     */
    @ApiModelProperty(value = "父权限ID", example = "0")
    private Long parentId;

    /**
     * 权限编码
     */
    @ApiModelProperty(value = "权限编码", required = true, example = "system:user:add")
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称", required = true, example = "添加用户")
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    /**
     * 权限类型（1: 菜单, 2: 按钮, 3: 接口）
     */
    @ApiModelProperty(value = "权限类型（1:菜单,2:按钮,3:接口）", required = true, example = "1")
    @NotNull(message = "权限类型不能为空")
    private Integer permissionType;

    /**
     * 路由路径
     */
    @ApiModelProperty(value = "路由路径", example = "/system/user")
    private String path;

    /**
     * 组件路径
     */
    @ApiModelProperty(value = "组件路径", example = "system/user/index")
    private String component;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标", example = "user")
    private String icon;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", example = "1")
    private Integer sortOrder;

    /**
     * 是否可见（0: 隐藏, 1: 显示）
     */
    @ApiModelProperty(value = "是否可见（0:隐藏,1:显示）", example = "1")
    private Integer visible;

    /**
     * 状态（0: 禁用, 1: 启用）
     */
    @ApiModelProperty(value = "状态（0:禁用,1:启用）", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "用户管理菜单")
    private String remark;

    /**
     * 子权限列表（用于树形结构）
     */
    @ApiModelProperty(value = "子权限列表")
    private List<PermissionDTO> children;

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
