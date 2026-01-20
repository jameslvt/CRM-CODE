package com.crm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.business.dto.OpportunityDTO;
import com.crm.business.dto.OpportunityProductDTO;
import com.crm.business.dto.OpportunityQueryParams;
import com.crm.business.dto.StageAdvanceDTO;
import com.crm.business.service.OpportunityService;
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
 * 商机管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "商机管理")
@RestController
@RequestMapping("/api/business/opportunity")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    /**
     * 分页查询商机列表
     */
    @ApiOperation("分页查询商机列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('business:opportunity:list')")
    public Result<IPage<OpportunityDTO>> getOpportunityPage(OpportunityQueryParams params) {
        IPage<OpportunityDTO> page = opportunityService.getOpportunityPage(params);
        return Result.success(page);
    }

    /**
     * 根据客户ID获取商机列表
     */
    @ApiOperation("根据客户ID获取商机列表")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAuthority('business:opportunity:list')")
    public Result<List<OpportunityDTO>> getOpportunitiesByCustomerId(@PathVariable Long customerId) {
        List<OpportunityDTO> opportunities = opportunityService.getOpportunitiesByCustomerId(customerId);
        return Result.success(opportunities);
    }

    /**
     * 根据ID获取商机详情
     */
    @ApiOperation("根据ID获取商机详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:opportunity:list')")
    public Result<OpportunityDTO> getOpportunityById(@PathVariable Long id) {
        OpportunityDTO opportunity = opportunityService.getOpportunityById(id);
        return Result.success(opportunity);
    }

    /**
     * 创建商机
     */
    @ApiOperation("创建商机")
    @PostMapping
    @PreAuthorize("hasAuthority('business:opportunity:add')")
    public Result<Long> createOpportunity(@Validated @RequestBody OpportunityDTO formData) {
        Long id = opportunityService.createOpportunity(formData);
        return Result.success(id);
    }

    /**
     * 更新商机
     */
    @ApiOperation("更新商机")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:opportunity:edit')")
    public Result<Void> updateOpportunity(@PathVariable Long id, @Validated @RequestBody OpportunityDTO formData) {
        opportunityService.updateOpportunity(id, formData);
        return Result.success();
    }

    /**
     * 删除商机
     */
    @ApiOperation("删除商机")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:opportunity:delete')")
    public Result<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return Result.success();
    }

    /**
     * 推进商机阶段
     */
    @ApiOperation("推进商机阶段")
    @PostMapping("/advance-stage")
    @PreAuthorize("hasAuthority('business:opportunity:edit')")
    public Result<Void> advanceStage(@Validated @RequestBody StageAdvanceDTO advanceDTO) {
        opportunityService.advanceStage(advanceDTO);
        return Result.success();
    }

    /**
     * 获取商机产品列表
     */
    @ApiOperation("获取商机产品列表")
    @GetMapping("/{id}/products")
    @PreAuthorize("hasAuthority('business:opportunity:list')")
    public Result<List<OpportunityProductDTO>> getOpportunityProducts(@PathVariable Long id) {
        List<OpportunityProductDTO> products = opportunityService.getOpportunityProducts(id);
        return Result.success(products);
    }

    /**
     * 获取商机统计数据
     */
    @ApiOperation("获取商机统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('business:opportunity:list')")
    public Result<Map<String, Object>> getOpportunityStatistics(
            @ApiParam("负责人ID") @RequestParam(required = false) Long ownerId) {
        Map<String, Object> statistics = opportunityService.getOpportunityStatistics(ownerId);
        return Result.success(statistics);
    }
}
