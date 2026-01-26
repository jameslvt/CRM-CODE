package com.crm.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门 DTO
 * 用于部门数据的传输和展示
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@ApiModel(value = "部门数据传输对象", description = "部门信息")
public class DepartmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门 ID
     */
    @ApiModelProperty(value = "部门ID", example = "1")
    private Long id;

    /**
     * 父部门 ID
     */
    @ApiModelProperty(value = "父部门ID", example = "0")
    private Long parentId;

    /**
     * 部门编码
     */
    @ApiModelProperty(value = "部门编码", required = true, example = "DEPT001")
    @NotBlank(message = "部门编码不能为空")
    private String code;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称", required = true, example = "技术部")
    @NotBlank(message = "部门名称不能为空")
    private String name;

    /**
     * 部门负责人 ID
     */
    @ApiModelProperty(value = "部门负责人ID", example = "1")
    private Long leaderId;

    /**
     * 部门负责人姓名
     */
    @ApiModelProperty(value = "部门负责人姓名", example = "张三")
    private String leaderName;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号", example = "1")
    private Integer sort;

    /**
     * 状态（0: 禁用, 1: 启用）
     */
    @ApiModelProperty(value = "状态（0:禁用,1:启用）", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "负责技术研发")
    private String remark;

    /**
     * 子部门列表（用于树形结构）
     */
    @ApiModelProperty(value = "子部门列表")
    private List<DepartmentDTO> children;

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
