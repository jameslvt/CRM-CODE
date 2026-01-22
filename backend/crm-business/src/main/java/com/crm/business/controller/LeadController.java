package com.crm.business.controller;

import com.crm.business.dto.*;
import com.crm.business.entity.Lead;
import com.crm.business.service.LeadService;
import com.crm.common.exception.BusinessException;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.common.utils.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 线索管理控制器
 *
 * @author CRM System
 * @since 2026-01-20
 */
@Api(tags = "线索管理")
@RestController
@RequestMapping("/api/business/lead")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    /**
     * 分页查询线索列表
     */
    @ApiOperation("分页查询线索列表")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('business:lead:list')")
    public Result<PageResult<LeadDTO>> getLeadList(LeadQueryParams params) {
        PageResult<LeadDTO> result = leadService.getLeadList(params);
        return Result.success(result);
    }

    /**
     * 根据ID获取线索详情
     */
    @ApiOperation("根据ID获取线索详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('business:lead:detail')")
    public Result<LeadDTO> getLeadById(@PathVariable Long id) {
        LeadDTO lead = leadService.getLeadById(id);
        return Result.success(lead);
    }

    /**
     * 创建线索
     */
    @ApiOperation("创建线索")
    @PostMapping
    @PreAuthorize("hasAuthority('business:lead:add')")
    public Result<Long> createLead(@Validated @RequestBody LeadFormData formData) {
        Long id = leadService.createLead(formData);
        return Result.success(id);
    }

    /**
     * 更新线索
     */
    @ApiOperation("更新线索")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('business:lead:edit')")
    public Result<Void> updateLead(@PathVariable Long id, @Validated @RequestBody LeadFormData formData) {
        leadService.updateLead(id, formData);
        return Result.success();
    }

    /**
     * 删除线索
     */
    @ApiOperation("删除线索")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('business:lead:delete')")
    public Result<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return Result.success();
    }

    /**
     * 批量删除线索
     */
    @ApiOperation("批量删除线索")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('business:lead:delete')")
    public Result<Void> batchDeleteLeads(@RequestBody List<Long> ids) {
        leadService.batchDeleteLeads(ids);
        return Result.success();
    }

    /**
     * 转化线索为客户
     */
    @ApiOperation("转化线索为客户")
    @PostMapping("/convert")
    @PreAuthorize("hasAuthority('business:lead:convert')")
    public Result<Long> convertLead(@Validated @RequestBody LeadConvertParams params) {
        Long customerId = leadService.convertLead(params);
        return Result.success(customerId);
    }

    /**
     * 导入线索
     */
    @ApiOperation("导入线索")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('business:lead:import')")
    public Result<Integer> importLeads(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 定义字段映射（Excel列顺序）
        String[] fieldNames = {
                "leadName", "contactName", "phone", "email",
                "companyName", "position", "source", "industry",
                "rating", "address", "remark"
        };

        List<Lead> dataList = ExcelUtils.importExcel(file, Lead.class, fieldNames);
        int successCount = leadService.batchImport(dataList);

        return Result.success(successCount);
    }

    /**
     * 导出线索
     */
    @ApiOperation("导出线索")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('business:lead:export')")
    public void exportLeads(LeadQueryParams params, HttpServletResponse response) {
        // 查询数据
        List<Lead> dataList = leadService.batchExport(params);

        // 定义表头和字段
        String[] headers = {
                "线索名称", "联系人", "联系电话", "邮箱",
                "公司名称", "职位", "来源", "行业",
                "评级", "地址", "备注"
        };
        String[] fieldNames = {
                "leadName", "contactName", "phone", "email",
                "companyName", "position", "source", "industry",
                "rating", "address", "remark"
        };

        // 导出
        String fileName = "线索数据_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        ExcelUtils.exportExcel(response, dataList, headers, fieldNames, fileName);
    }

    /**
     * 获取线索统计
     */
    @ApiOperation("获取线索统计")
    @GetMapping("/stats")
    public Result<java.util.Map<String, Object>> getLeadStats() {
        java.util.Map<String, Object> stats = leadService.getLeadStats();
        return Result.success(stats);
    }
}
