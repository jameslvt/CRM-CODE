package com.crm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.business.dto.ContractDTO;
import com.crm.business.dto.ContractQueryParams;
import com.crm.business.service.ContractService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 合同管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "合同管理")
@RestController
@RequestMapping("/api/business/contract")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * 分页查询合同列表
     */
    @ApiOperation("分页查询合同列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('business:contract:list')")
    public Result<IPage<ContractDTO>> getContractPage(ContractQueryParams params) {
        IPage<ContractDTO> page = contractService.getContractPage(params);
        return Result.success(page);
    }

    /**
     * 根据客户ID获取合同列表
     */
    @ApiOperation("根据客户ID获取合同列表")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('business:contract:list')")
    public Result<List<ContractDTO>> getContractsByCustomerId(@PathVariable Long customerId) {
        List<ContractDTO> contracts = contractService.getContractsByCustomerId(customerId);
        return Result.success(contracts);
    }

    /**
     * 根据商机ID获取合同列表
     */
    @ApiOperation("根据商机ID获取合同列表")
    @GetMapping("/opportunity/{opportunityId}")
    @PreAuthorize("hasAuthority('business:contract:list')")
    public Result<List<ContractDTO>> getContractsByOpportunityId(@PathVariable Long opportunityId) {
        List<ContractDTO> contracts = contractService.getContractsByOpportunityId(opportunityId);
        return Result.success(contracts);
    }

    /**
     * 根据ID获取合同详情
     */
    @ApiOperation("根据ID获取合同详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contract:list')")
    public Result<ContractDTO> getContractById(@PathVariable Long id) {
        ContractDTO contract = contractService.getContractById(id);
        return Result.success(contract);
    }

    /**
     * 创建合同
     */
    @ApiOperation("创建合同")
    @PostMapping
    @PreAuthorize("hasAuthority('business:contract:add')")
    public Result<Long> createContract(@Validated @RequestBody ContractDTO formData) {
        Long id = contractService.createContract(formData);
        return Result.success(id);
    }

    /**
     * 从商机创建合同
     */
    @ApiOperation("从商机创建合同")
    @PostMapping("/from-opportunity/{opportunityId}")
    @PreAuthorize("hasAuthority('business:contract:add')")
    public Result<Long> createFromOpportunity(@PathVariable Long opportunityId) {
        Long id = contractService.createFromOpportunity(opportunityId);
        return Result.success(id);
    }

    /**
     * 更新合同
     */
    @ApiOperation("更新合同")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contract:edit')")
    public Result<Void> updateContract(@PathVariable Long id, @Validated @RequestBody ContractDTO formData) {
        contractService.updateContract(id, formData);
        return Result.success();
    }

    /**
     * 删除合同
     */
    @ApiOperation("删除合同")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:contract:delete')")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    /**
     * 提交审批
     */
    @ApiOperation("提交审批")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('business:contract:edit')")
    public Result<Void> submitForApproval(@PathVariable Long id) {
        contractService.submitForApproval(id);
        return Result.success();
    }

    /**
     * 审批通过
     */
    @ApiOperation("审批通过")
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('business:contract:approve')")
    public Result<Void> approve(@PathVariable Long id) {
        contractService.approve(id);
        return Result.success();
    }

    /**
     * 审批驳回
     */
    @ApiOperation("审批驳回")
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('business:contract:approve')")
    public Result<Void> reject(@PathVariable Long id) {
        contractService.reject(id);
        return Result.success();
    }

    /**
     * 完成合同
     */
    @ApiOperation("完成合同")
    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('business:contract:edit')")
    public Result<Void> complete(@PathVariable Long id) {
        contractService.complete(id);
        return Result.success();
    }

    /**
     * 终止合同
     */
    @ApiOperation("终止合同")
    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAuthority('business:contract:edit')")
    public Result<Void> terminate(@PathVariable Long id) {
        contractService.terminate(id);
        return Result.success();
    }

    /**
     * 更新合同文件
     */
    @ApiOperation("更新合同文件")
    @PutMapping("/{id}/file")
    @PreAuthorize("hasAuthority('business:contract:edit')")
    public Result<Void> updateFileUrl(@PathVariable Long id, @RequestParam String fileUrl) {
        contractService.updateFileUrl(id, fileUrl);
        return Result.success();
    }

    /**
     * 获取合同统计数据
     */
    @ApiOperation("获取合同统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('business:contract:list')")
    public Result<Map<String, Object>> getContractStatistics(
            @ApiParam("负责人ID") @RequestParam(required = false) Long ownerId) {
        Map<String, Object> statistics = contractService.getContractStatistics(ownerId);
        return Result.success(statistics);
    }
}
