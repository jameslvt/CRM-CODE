package com.crm.business.controller;

import com.crm.business.dto.*;
import com.crm.business.service.CustomerService;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "客户管理")
@RestController
@RequestMapping("/api/business/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 分页查询客户列表
     */
    @ApiOperation("分页查询客户列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('business:customer:list')")
    public Result<PageResult<CustomerDTO>> getCustomerList(CustomerQueryParams params) {
        PageResult<CustomerDTO> result = customerService.getCustomerList(params);
        return Result.success(result);
    }

    /**
     * 根据ID获取客户详情
     */
    @ApiOperation("根据ID获取客户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:detail')")
    public Result<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return Result.success(customer);
    }

    /**
     * 获取客户360度视图
     */
    @ApiOperation("获取客户360度视图")
    @GetMapping("/{id}/360")
    @PreAuthorize("hasAuthority('business:customer:detail')")
    public Result<Customer360DTO> getCustomer360(@PathVariable Long id) {
        Customer360DTO customer360 = customerService.getCustomer360(id);
        return Result.success(customer360);
    }

    /**
     * 创建客户
     */
    @ApiOperation("创建客户")
    @PostMapping
    @PreAuthorize("hasAuthority('business:customer:add')")
    public Result<Long> createCustomer(@Validated @RequestBody CustomerFormData formData) {
        Long id = customerService.createCustomer(formData);
        return Result.success(id);
    }

    /**
     * 更新客户
     */
    @ApiOperation("更新客户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:edit')")
    public Result<Void> updateCustomer(@PathVariable Long id, @Validated @RequestBody CustomerFormData formData) {
        customerService.updateCustomer(id, formData);
        return Result.success();
    }

    /**
     * 删除客户
     */
    @ApiOperation("删除客户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:customer:delete')")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success();
    }

    /**
     * 释放客户到公海
     */
    @ApiOperation("释放客户到公海")
    @PostMapping("/{id}/release")
    @PreAuthorize("hasAuthority('business:customer:release')")
    public Result<Void> releaseToPool(@PathVariable Long id, @RequestBody(required = false) ReleaseRequest request) {
        String reason = request != null ? request.getReason() : null;
        customerService.releaseToPool(id, reason);
        return Result.success();
    }

    /**
     * 从公海领取客户
     */
    @ApiOperation("从公海领取客户")
    @PostMapping("/{id}/acquire")
    @PreAuthorize("hasAuthority('business:customer:acquire')")
    public Result<Void> acquireFromPool(@PathVariable Long id, @RequestAttribute(value = "userId", required = false) Long userId) {
        // TODO: 从当前登录用户获取userId
        customerService.acquireFromPool(id, userId);
        return Result.success();
    }

    /**
     * 分配客户
     */
    @ApiOperation("分配客户")
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('business:customer:assign')")
    public Result<Void> assignCustomer(@PathVariable Long id, @RequestBody AssignRequest request) {
        customerService.assignCustomer(id, request.getOwnerId());
        return Result.success();
    }

    /**
     * 释放请求
     */
    @lombok.Data
    public static class ReleaseRequest {
        private String reason;
    }

    /**
     * 分配请求
     */
    @lombok.Data
    public static class AssignRequest {
        private Long ownerId;
    }
}
