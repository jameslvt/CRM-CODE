package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class ProductDTO {

    /**
     * 产品ID
     */
    private Long id;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(min = 2, max = 200, message = "产品名称长度必须在2-200个字符之间")
    private String name;

    /**
     * 产品编码
     */
    @Size(max = 50, message = "产品编码长度不能超过50个字符")
    private String code;

    /**
     * 分类
     */
    @Size(max = 50, message = "分类长度不能超过50个字符")
    private String category;

    /**
     * 计量单位
     */
    @Size(max = 20, message = "计量单位长度不能超过20个字符")
    private String unit;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0", message = "单价不能为负数")
    private BigDecimal price;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态: 0-停用, 1-启用
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
