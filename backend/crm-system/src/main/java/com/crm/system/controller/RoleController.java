package com.crm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.common.result.Result;
import com.crm.system.dto.RoleDTO;
import com.crm.system.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 角色控制器
 * 提供角色管理的 RESTful API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Api(tags = "角色管理")
@RestController
@RequestMapping("/api/system/roles")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 创建角色
     *
     * @param roleDTO 角色 DTO
     * @return 角色 ID
     */
    @ApiOperation(value = "创建角色", notes = "创建新角色")
    @PostMapping
    public Result<Long> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("创建角色: {}", roleDTO.getRoleName());
        Long roleId = roleService.createRole(roleDTO);
        return Result.success(roleId);
    }

    /**
     * 更新角色
     *
     * @param id      角色 ID
     * @param roleDTO 角色 DTO
     * @return 操作结果
     */
    @ApiOperation(value = "更新角色", notes = "更新角色信息")
    @PutMapping("/{id}")
    public Result<Void> updateRole(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long id,
            @Valid @RequestBody RoleDTO roleDTO) {
        log.info("更新角色: {}", id);
        roleDTO.setId(id);
        roleService.updateRole(roleDTO);
        return Result.success();
    }

    /**
     * 删除角色
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除角色", notes = "删除指定角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        log.info("删除角色: {}", id);
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @ApiOperation(value = "查询角色详情", notes = "根据ID查询角色详细信息")
    @GetMapping("/{id}")
    public Result<RoleDTO> getRoleById(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        log.info("查询角色: {}", id);
        RoleDTO roleDTO = roleService.getRoleById(id);
        return Result.success(roleDTO);
    }

    /**
     * 分页查询角色列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param roleName 角色名称（模糊查询）
     * @param status   状态
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询角色", notes = "分页查询角色列表，支持多条件筛选")
    @GetMapping
    public Result<IPage<RoleDTO>> getRolePage(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "角色名称") @RequestParam(required = false) String roleName,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询角色列表");
        IPage<RoleDTO> rolePage = roleService.getRolePage(page, size, roleName, status);
        return Result.success(rolePage);
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @ApiOperation(value = "查询所有角色", notes = "查询所有启用状态的角色")
    @GetMapping("/all")
    public Result<List<RoleDTO>> getAllRoles() {
        log.info("查询所有角色");
        List<RoleDTO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 为角色分配权限
     *
     * @param id            角色 ID
     * @param permissionIds 权限 ID 列表
     * @return 操作结果
     */
    @ApiOperation(value = "分配权限", notes = "为角色分配权限")
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long id,
            @ApiParam(value = "权限ID列表", required = true) @RequestBody List<Long> permissionIds) {
        log.info("为角色 {} 分配权限: {}", id, permissionIds);
        roleService.assignPermissions(id, permissionIds);
        return Result.success();
    }

    /**
     * 查询角色的权限 ID 列表
     *
     * @param id 角色 ID
     * @return 权限 ID 列表
     */
    @ApiOperation(value = "查询角色权限", notes = "查询角色拥有的权限ID列表")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getPermissionIdsByRoleId(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        log.info("查询角色 {} 的权限", id);
        List<Long> permissionIds = roleService.getPermissionIdsByRoleId(id);
        return Result.success(permissionIds);
    }
}
