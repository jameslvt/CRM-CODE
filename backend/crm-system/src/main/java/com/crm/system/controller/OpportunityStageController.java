package com.crm.system.controller;

import com.crm.common.result.Result;
import com.crm.system.entity.OpportunityStage;
import com.crm.system.service.OpportunityStageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商机阶段配置控制器
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Api(tags = "商机阶段配置")
@RestController
@RequestMapping("/system/opportunity-stage")
@RequiredArgsConstructor
public class OpportunityStageController {

    private final OpportunityStageService opportunityStageService;

    /**
     * 查询所有商机阶段
     */
    @ApiOperation("查询所有商机阶段")
    @GetMapping("/list")
    public Result<List<OpportunityStage>> listAllStages() {
        return Result.success(opportunityStageService.listAllStages());
    }

    /**
     * 根据ID查询商机阶段
     */
    @ApiOperation("根据ID查询商机阶段")
    @GetMapping("/{id}")
    public Result<OpportunityStage> getStageById(@PathVariable Long id) {
        return Result.success(opportunityStageService.getById(id));
    }

    /**
     * 新增商机阶段
     */
    @ApiOperation("新增商机阶段")
    @PostMapping
    public Result<Void> saveStage(@RequestBody OpportunityStage stage) {
        opportunityStageService.saveStage(stage);
        return Result.success();
    }

    /**
     * 修改商机阶段
     */
    @ApiOperation("修改商机阶段")
    @PutMapping
    public Result<Void> updateStage(@RequestBody OpportunityStage stage) {
        opportunityStageService.updateStage(stage);
        return Result.success();
    }

    /**
     * 删除商机阶段
     */
    @ApiOperation("删除商机阶段")
    @DeleteMapping("/{id}")
    public Result<Void> deleteStage(@PathVariable Long id) {
        opportunityStageService.deleteStage(id);
        return Result.success();
    }

    /**
     * 调整阶段排序
     */
    @ApiOperation("调整阶段排序")
    @PutMapping("/{id}/sort")
    public Result<Void> updateSort(@PathVariable Long id, @RequestParam Integer sort) {
        opportunityStageService.updateSort(id, sort);
        return Result.success();
    }
}
