package com.crm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.common.result.Result;
import com.crm.system.dto.PermissionDTO;
import com.crm.system.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 权限控制器
 * 提供权限管理的 RESTful API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Api(tags = "权限管理")
@RestController
@RequestMapping("/api/system/permissions")
public class PermissionController {

    @Resource(name = "permissionManageService")
    private PermissionService permissionService;

    /**
     * 创建权限
     *
     * @param permissionDTO 权限 DTO
     * @return 权限 ID
     */
    @ApiOperation(value = "创建权限", notes = "创建新权限")
    @PostMapping
    public Result<Long> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("创建权限: {}", permissionDTO.getPermissionName());
        Long permissionId = permissionService.createPermission(permissionDTO);
        return Result.success(permissionId);
    }

    /**
     * 更新权限
     *
     * @param id            权限 ID
     * @param permissionDTO 权限 DTO
     * @return 操作结果
     */
    @ApiOperation(value = "更新权限", notes = "更新权限信息")
    @PutMapping("/{id}")
    public Result<Void> updatePermission(
            @ApiParam(value = "权限ID", required = true) @PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        log.info("更新权限: {}", id);
        permissionDTO.setId(id);
        permissionService.updatePermission(permissionDTO);
        return Result.success();
    }

    /**
     * 删除权限
     *
     * @param id 权限 ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除权限", notes = "删除指定权限")
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(
            @ApiParam(value = "权限ID", required = true) @PathVariable Long id) {
        log.info("删除权限: {}", id);
        permissionService.deletePermission(id);
        return Result.success();
    }

    /**
     * 根据 ID 查询权限
     *
     * @param id 权限 ID
     * @return 权限信息
     */
    @ApiOperation(value = "查询权限详情", notes = "根据ID查询权限详细信息")
    @GetMapping("/{id}")
    public Result<PermissionDTO> getPermissionById(
            @ApiParam(value = "权限ID", required = true) @PathVariable Long id) {
        log.info("查询权限: {}", id);
        PermissionDTO permissionDTO = permissionService.getPermissionById(id);
        return Result.success(permissionDTO);
    }

    /**
     * 分页查询权限列表
     *
     * @param page           页码
     * @param size           每页大小
     * @param permissionName 权限名称（模糊查询）
     * @param permissionType 权限类型
     * @param status         状态
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询权限", notes = "分页查询权限列表，支持多条件筛选")
    @GetMapping
    public Result<IPage<PermissionDTO>> getPermissionPage(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "权限名称") @RequestParam(required = false) String permissionName,
            @ApiParam(value = "权限类型") @RequestParam(required = false) Integer permissionType,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询权限列表");
        IPage<PermissionDTO> permissionPage = permissionService.getPermissionPage(page, size, permissionName, permissionType, status);
        return Result.success(permissionPage);
    }

    /**
     * 查询权限树
     *
     * @return 权限树列表
     */
    @ApiOperation(value = "查询权限树", notes = "查询所有权限的树形结构")
    @GetMapping("/tree")
    public Result<List<PermissionDTO>> getPermissionTree() {
        log.info("查询权限树");
        List<PermissionDTO> permissionTree = permissionService.getPermissionTree();
        return Result.success(permissionTree);
    }

    /**
     * 根据角色 ID 查询权限列表
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    @ApiOperation(value = "查询角色权限", notes = "根据角色ID查询权限列表")
    @GetMapping("/by-role/{roleId}")
    public Result<List<PermissionDTO>> getPermissionsByRoleId(
            @ApiParam(value = "角色ID", required = true) @PathVariable Long roleId) {
        log.info("查询角色 {} 的权限", roleId);
        List<PermissionDTO> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }

    /**
     * 根据用户 ID 查询权限列表
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    @ApiOperation(value = "查询用户权限", notes = "根据用户ID查询权限列表")
    @GetMapping("/by-user/{userId}")
    public Result<List<PermissionDTO>> getPermissionsByUserId(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long userId) {
        log.info("查询用户 {} 的权限", userId);
        List<PermissionDTO> permissions = permissionService.getPermissionsByUserId(userId);
        return Result.success(permissions);
    }
}
