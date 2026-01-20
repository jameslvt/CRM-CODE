package com.crm.business.controller;

import com.crm.business.dto.DashboardDTO;
import com.crm.business.dto.PerformanceTrendDTO;
import com.crm.business.dto.SalesFunnelDTO;
import com.crm.business.service.DashboardService;
import com.crm.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 仪表盘控制器
 * 提供销售数据可视化接口
 *
 * @author CRM System
 * @since 1.0.0
 */
@Api(tags = "仪表盘")
@RestController
@RequestMapping("/api/business/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 获取仪表盘汇总数据
     */
    @ApiOperation("获取仪表盘汇总数据")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('business:dashboard:view')")
    public Result<DashboardDTO> getDashboardData(
            @ApiParam("负责人ID（为空则查询全部）") @RequestParam(required = false) Long ownerId) {
        DashboardDTO dashboard = dashboardService.getDashboardData(ownerId);
        return Result.success(dashboard);
    }

    /**
     * 获取销售漏斗数据
     */
    @ApiOperation("获取销售漏斗数据")
    @GetMapping("/funnel")
    @PreAuthorize("hasAuthority('business:dashboard:view')")
    public Result<SalesFunnelDTO> getSalesFunnel(
            @ApiParam("负责人ID（为空则查询全部）") @RequestParam(required = false) Long ownerId) {
        SalesFunnelDTO funnel = dashboardService.getSalesFunnel(ownerId);
        return Result.success(funnel);
    }

    /**
     * 获取业绩趋势数据
     */
    @ApiOperation("获取业绩趋势数据")
    @GetMapping("/trend")
    @PreAuthorize("hasAuthority('business:dashboard:view')")
    public Result<PerformanceTrendDTO> getPerformanceTrend(
            @ApiParam("负责人ID（为空则查询全部）") @RequestParam(required = false) Long ownerId,
            @ApiParam("周期类型: month、quarter") @RequestParam(defaultValue = "month") String periodType,
            @ApiParam("查询月数") @RequestParam(defaultValue = "12") Integer months) {
        PerformanceTrendDTO trend = dashboardService.getPerformanceTrend(ownerId, periodType, months);
        return Result.success(trend);
    }
}
