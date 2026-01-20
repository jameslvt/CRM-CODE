package com.crm.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.business.dto.ActivityDTO;
import com.crm.business.service.ActivityService;
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
 * 跟进记录管理控制器
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "跟进记录管理")
@RestController
@RequestMapping("/api/business/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /**
     * 分页查询跟进记录列表
     */
    @ApiOperation("分页查询跟进记录列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('business:activity:list')")
    public Result<IPage<ActivityDTO>> getActivityPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("关联对象类型") @RequestParam(required = false) String targetType,
            @ApiParam("关联对象ID") @RequestParam(required = false) Long targetId,
            @ApiParam("跟进类型") @RequestParam(required = false) String type,
            @ApiParam("创建人ID") @RequestParam(required = false) Long createBy) {
        IPage<ActivityDTO> page = activityService.getActivityPage(pageNum, pageSize, targetType, targetId, type, createBy);
        return Result.success(page);
    }

    /**
     * 根据关联对象获取跟进记录列表（时间线）
     */
    @ApiOperation("根据关联对象获取跟进记录列表")
    @GetMapping("/target/{targetType}/{targetId}")
    @PreAuthorize("hasAuthority('business:activity:list')")
    public Result<List<ActivityDTO>> getActivitiesByTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        List<ActivityDTO> activities = activityService.getActivitiesByTarget(targetType, targetId);
        return Result.success(activities);
    }

    /**
     * 根据ID获取跟进记录详情
     */
    @ApiOperation("根据ID获取跟进记录详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:activity:list')")
    public Result<ActivityDTO> getActivityById(@PathVariable Long id) {
        ActivityDTO activity = activityService.getActivityById(id);
        return Result.success(activity);
    }

    /**
     * 创建跟进记录
     */
    @ApiOperation("创建跟进记录")
    @PostMapping
    @PreAuthorize("hasAuthority('business:activity:add')")
    public Result<Long> createActivity(@Validated @RequestBody ActivityDTO formData) {
        Long id = activityService.createActivity(formData);
        return Result.success(id);
    }

    /**
     * 更新跟进记录
     */
    @ApiOperation("更新跟进记录")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:activity:edit')")
    public Result<Void> updateActivity(@PathVariable Long id, @Validated @RequestBody ActivityDTO formData) {
        activityService.updateActivity(id, formData);
        return Result.success();
    }

    /**
     * 删除跟进记录
     */
    @ApiOperation("删除跟进记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:activity:delete')")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success();
    }

    /**
     * 获取今日待跟进列表
     */
    @ApiOperation("获取今日待跟进列表")
    @GetMapping("/today-pending")
    @PreAuthorize("hasAuthority('business:activity:list')")
    public Result<List<ActivityDTO>> getTodayPendingActivities(
            @ApiParam("用户ID") @RequestParam Long userId) {
        List<ActivityDTO> activities = activityService.getTodayPendingActivities(userId);
        return Result.success(activities);
    }

    /**
     * 获取跟进统计数据
     */
    @ApiOperation("获取跟进统计数据")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('business:activity:list')")
    public Result<Map<String, Object>> getActivityStatistics(
            @ApiParam("关联对象类型") @RequestParam(required = false) String targetType,
            @ApiParam("关联对象ID") @RequestParam(required = false) Long targetId) {
        Map<String, Object> statistics = activityService.getActivityStatistics(targetType, targetId);
        return Result.success(statistics);
    }
}
