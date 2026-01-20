package com.crm.business.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 客户标签 DTO
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class CustomerTagDTO {

    /**
     * 标签ID（更新时使用）
     */
    private Long id;

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String name;

    /**
     * 标签颜色（十六进制颜色值）
     */
    @Size(max = 20, message = "颜色值长度不能超过20个字符")
    private String color;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
