package com.crm.business.controller;

import com.crm.business.dto.CustomerTagDTO;
import com.crm.business.entity.CustomerTag;
import com.crm.business.service.CustomerTagService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户标签管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "客户标签管理")
@RestController
@RequestMapping("/api/business/customer-tag")
@RequiredArgsConstructor
public class CustomerTagController {

    private final CustomerTagService customerTagService;

    /**
     * 获取所有标签列表
     */
    @ApiOperation("获取所有标签列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('business:customer:list')")
    public Result<List<CustomerTag>> getAllTags() {
        List<CustomerTag> tags = customerTagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 根据ID获取标签
     */
    @ApiOperation("根据ID获取标签")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:list')")
    public Result<CustomerTag> getTagById(@PathVariable Long id) {
        CustomerTag tag = customerTagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 创建标签
     */
    @ApiOperation("创建标签")
    @PostMapping
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Long> createTag(@Validated @RequestBody CustomerTagDTO formData) {
        Long id = customerTagService.createTag(formData);
        return Result.success(id);
    }

    /**
     * 更新标签
     */
    @ApiOperation("更新标签")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> updateTag(@PathVariable Long id, @Validated @RequestBody CustomerTagDTO formData) {
        customerTagService.updateTag(id, formData);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @ApiOperation("删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> deleteTag(@PathVariable Long id) {
        customerTagService.deleteTag(id);
        return Result.success();
    }

    /**
     * 获取客户的标签列表
     */
    @ApiOperation("获取客户的标签列表")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('business:customer:list')")
    public Result<List<CustomerTag>> getTagsByCustomerId(@PathVariable Long customerId) {
        List<CustomerTag> tags = customerTagService.getTagsByCustomerId(customerId);
        return Result.success(tags);
    }

    /**
     * 设置客户的标签
     */
    @ApiOperation("设置客户的标签")
    @PutMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> setCustomerTags(@PathVariable Long customerId, @RequestBody List<Long> tagIds) {
        customerTagService.setCustomerTags(customerId, tagIds);
        return Result.success();
    }

    /**
     * 为客户添加标签
     */
    @ApiOperation("为客户添加标签")
    @PostMapping("/customer/{customerId}/tag/{tagId}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> addTagToCustomer(@PathVariable Long customerId, @PathVariable Long tagId) {
        customerTagService.addTagToCustomer(customerId, tagId);
        return Result.success();
    }

    /**
     * 移除客户的标签
     */
    @ApiOperation("移除客户的标签")
    @DeleteMapping("/customer/{customerId}/tag/{tagId}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> removeTagFromCustomer(@PathVariable Long customerId, @PathVariable Long tagId) {
        customerTagService.removeTagFromCustomer(customerId, tagId);
        return Result.success();
    }
}
