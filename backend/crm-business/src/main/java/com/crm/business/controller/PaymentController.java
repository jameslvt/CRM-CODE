package com.crm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.business.dto.PaymentPlanDTO;
import com.crm.business.dto.PaymentRecordDTO;
import com.crm.business.service.PaymentService;
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
 * 回款管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "回款管理")
@RestController
@RequestMapping("/api/business/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ==================== 回款计划 ====================

    /**
     * 分页查询回款计划列表
     */
    @ApiOperation("分页查询回款计划列表")
    @GetMapping("/plan/page")
    @PreAuthorize("hasAuthority('business:payment:list')")
    public Result<IPage<PaymentPlanDTO>> getPaymentPlanPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("合同ID") @RequestParam(required = false) Long contractId,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        IPage<PaymentPlanDTO> page = paymentService.getPaymentPlanPage(pageNum, pageSize, contractId, status);
        return Result.success(page);
    }

    /**
     * 根据合同ID获取回款计划列表
     */
    @ApiOperation("根据合同ID获取回款计划列表")
    @GetMapping("/plan/contract/{contractId}")
    @PreAuthorize("hasAuthority('business:payment:list')")
    public Result<List<PaymentPlanDTO>> getPaymentPlansByContractId(@PathVariable Long contractId) {
        List<PaymentPlanDTO> plans = paymentService.getPaymentPlansByContractId(contractId);
        return Result.success(plans);
    }

    /**
     * 根据ID获取回款计划详情
     */
    @ApiOperation("根据ID获取回款计划详情")
    @GetMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('business:payment:list')")
    public Result<PaymentPlanDTO> getPaymentPlanById(@PathVariable Long id) {
        PaymentPlanDTO plan = paymentService.getPaymentPlanById(id);
        return Result.success(plan);
    }

    /**
     * 创建回款计划
     */
    @ApiOperation("创建回款计划")
    @PostMapping("/plan")
    @PreAuthorize("hasAuthority('business:payment:add')")
    public Result<Long> createPaymentPlan(@Validated @RequestBody PaymentPlanDTO formData) {
        Long id = paymentService.createPaymentPlan(formData);
        return Result.success(id);
    }

    /**
     * 批量创建回款计划
     */
    @ApiOperation("批量创建回款计划")
    @PostMapping("/plan/batch/{contractId}")
    @PreAuthorize("hasAuthority('business:payment:add')")
    public Result<Void> batchCreatePaymentPlans(
            @PathVariable Long contractId,
            @Validated @RequestBody List<PaymentPlanDTO> plans) {
        paymentService.batchCreatePaymentPlans(contractId, plans);
        return Result.success();
    }

    /**
     * 更新回款计划
     */
    @ApiOperation("更新回款计划")
    @PutMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('business:payment:edit')")
    public Result<Void> updatePaymentPlan(@PathVariable Long id, @Validated @RequestBody PaymentPlanDTO formData) {
        paymentService.updatePaymentPlan(id, formData);
        return Result.success();
    }

    /**
     * 删除回款计划
     */
    @ApiOperation("删除回款计划")
    @DeleteMapping("/plan/{id}")
    @PreAuthorize("hasAuthority('business:payment:delete')")
    public Result<Void> deletePaymentPlan(@PathVariable Long id) {
        paymentService.deletePaymentPlan(id);
        return Result.success();
    }

    // ==================== 回款记录 ====================

    /**
     * 获取回款计划的回款记录列表
     */
    @ApiOperation("获取回款计划的回款记录列表")
    @GetMapping("/record/plan/{planId}")
    @PreAuthorize("hasAuthority('business:payment:list')")
    public Result<List<PaymentRecordDTO>> getPaymentRecordsByPlanId(@PathVariable Long planId) {
        List<PaymentRecordDTO> records = paymentService.getPaymentRecordsByPlanId(planId);
        return Result.success(records);
    }

    /**
     * 创建回款记录
     */
    @ApiOperation("创建回款记录")
    @PostMapping("/record")
    @PreAuthorize("hasAuthority('business:payment:add')")
    public Result<Long> createPaymentRecord(@Validated @RequestBody PaymentRecordDTO formData) {
        Long id = paymentService.createPaymentRecord(formData);
        return Result.success(id);
    }

    /**
     * 删除回款记录
     */
    @ApiOperation("删除回款记录")
    @DeleteMapping("/record/{id}")
    @PreAuthorize("hasAuthority('business:payment:delete')")
    public Result<Void> deletePaymentRecord(@PathVariable Long id) {
        paymentService.deletePaymentRecord(id);
        return Result.success();
    }

    // ==================== 统计 ====================

    /**
     * 获取回款统计数据
     */
    @ApiOperation("获取回款统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('business:payment:list')")
    public Result<Map<String, Object>> getPaymentStatistics(
            @ApiParam("合同ID") @RequestParam(required = false) Long contractId) {
        Map<String, Object> statistics = paymentService.getPaymentStatistics(contractId);
        return Result.success(statistics);
    }
}
