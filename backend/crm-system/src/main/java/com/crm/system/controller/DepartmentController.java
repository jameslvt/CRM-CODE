package com.crm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.common.result.Result;
import com.crm.system.dto.DepartmentDTO;
import com.crm.system.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 部门控制器
 * 提供部门管理的 RESTful API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Api(tags = "部门管理")
@RestController
@RequestMapping("/api/system/departments")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 创建部门
     *
     * @param departmentDTO 部门 DTO
     * @return 部门 ID
     */
    @ApiOperation(value = "创建部门", notes = "创建新部门")
    @PostMapping
    public Result<Long> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("创建部门: {}", departmentDTO.getName());
        Long departmentId = departmentService.createDepartment(departmentDTO);
        return Result.success(departmentId);
    }

    /**
     * 更新部门
     *
     * @param id            部门 ID
     * @param departmentDTO 部门 DTO
     * @return 操作结果
     */
    @ApiOperation(value = "更新部门", notes = "更新部门信息")
    @PutMapping("/{id}")
    public Result<Void> updateDepartment(
            @ApiParam(value = "部门ID", required = true) @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("更新部门: {}", id);
        departmentDTO.setId(id);
        departmentService.updateDepartment(departmentDTO);
        return Result.success();
    }

    /**
     * 删除部门
     *
     * @param id 部门 ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除部门", notes = "删除指定部门")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(
            @ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        log.info("删除部门: {}", id);
        departmentService.deleteDepartment(id);
        return Result.success();
    }

    /**
     * 根据 ID 查询部门
     *
     * @param id 部门 ID
     * @return 部门信息
     */
    @ApiOperation(value = "查询部门详情", notes = "根据ID查询部门详细信息")
    @GetMapping("/{id}")
    public Result<DepartmentDTO> getDepartmentById(
            @ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        log.info("查询部门: {}", id);
        DepartmentDTO departmentDTO = departmentService.getDepartmentById(id);
        return Result.success(departmentDTO);
    }

    /**
     * 分页查询部门列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param deptName 部门名称（模糊查询）
     * @param status   状态
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询部门", notes = "分页查询部门列表，支持多条件筛选")
    @GetMapping
    public Result<IPage<DepartmentDTO>> getDepartmentPage(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "部门名称") @RequestParam(required = false) String deptName,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询部门列表");
        IPage<DepartmentDTO> departmentPage = departmentService.getDepartmentPage(page, size, deptName, status);
        return Result.success(departmentPage);
    }

    /**
     * 查询部门树
     *
     * @return 部门树列表
     */
    @ApiOperation(value = "查询部门树", notes = "查询所有部门的树形结构")
    @GetMapping("/tree")
    public Result<List<DepartmentDTO>> getDepartmentTree() {
        log.info("查询部门树");
        List<DepartmentDTO> departmentTree = departmentService.getDepartmentTree();
        return Result.success(departmentTree);
    }

    /**
     * 根据父部门 ID 查询子部门列表
     *
     * @param parentId 父部门 ID
     * @return 部门列表
     */
    @ApiOperation(value = "查询子部门", notes = "根据父部门ID查询子部门列表")
    @GetMapping("/children/{parentId}")
    public Result<List<DepartmentDTO>> getDepartmentsByParentId(
            @ApiParam(value = "父部门ID", required = true) @PathVariable Long parentId) {
        log.info("查询父部门 {} 的子部门", parentId);
        List<DepartmentDTO> departments = departmentService.getDepartmentsByParentId(parentId);
        return Result.success(departments);
    }
}
