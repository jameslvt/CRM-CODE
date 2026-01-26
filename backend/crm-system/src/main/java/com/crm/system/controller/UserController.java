package com.crm.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.common.result.Result;
import com.crm.system.dto.UserDTO;
import com.crm.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 提供用户管理的 RESTful API
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/system/users")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 创建用户
     *
     * @param userDTO 用户 DTO
     * @return 用户 ID
     */
    @ApiOperation(value = "创建用户", notes = "创建新用户")
    @PostMapping
    public Result<Long> createUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("创建用户: {}", userDTO.getUsername());
        Long userId = userService.createUser(userDTO);
        return Result.success(userId);
    }

    /**
     * 更新用户
     *
     * @param id      用户 ID
     * @param userDTO 用户 DTO
     * @return 操作结果
     */
    @ApiOperation(value = "更新用户", notes = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UserDTO userDTO) {
        log.info("更新用户: {}", id);
        userDTO.setId(id);
        userService.updateUser(userDTO);
        return Result.success();
    }

    /**
     * 删除用户
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除用户", notes = "删除指定用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        log.info("删除用户: {}", id);
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @ApiOperation(value = "查询用户详情", notes = "根据ID查询用户详细信息")
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        log.info("查询用户: {}", id);
        UserDTO userDTO = userService.getUserById(id);
        return Result.success(userDTO);
    }

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @ApiOperation(value = "获取所有用户", notes = "获取所有启用状态的用户列表")
    @GetMapping("/all")
    public Result<List<UserDTO>> getAllUsers() {
        log.info("获取所有用户列表");
        List<UserDTO> userList = userService.getAllUsers();
        return Result.success(userList);
    }

    /**
     * 分页查询用户列表
     *
     * @param page         页码
     * @param size         每页大小
     * @param username     用户名（模糊查询）
     * @param realName     真实姓名（模糊查询）
     * @param departmentId 部门 ID
     * @param status       状态
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询用户", notes = "分页查询用户列表，支持多条件筛选")
    @GetMapping
    public Result<IPage<UserDTO>> getUserPage(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "用户名") @RequestParam(required = false) String username,
            @ApiParam(value = "真实姓名") @RequestParam(required = false) String realName,
            @ApiParam(value = "部门ID") @RequestParam(required = false) Long departmentId,
            @ApiParam(value = "状态") @RequestParam(required = false) Integer status) {
        log.info("分页查询用户列表");
        IPage<UserDTO> userPage = userService.getUserPage(page, size, username, realName, departmentId, status);
        return Result.success(userPage);
    }

    /**
     * 为用户分配角色
     *
     * @param id      用户 ID
     * @param roleIds 角色 ID 列表
     * @return 操作结果
     */
    @ApiOperation(value = "分配角色", notes = "为用户分配角色")
    @PostMapping("/{id}/roles")
    public Result<Void> assignRoles(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "角色ID列表", required = true) @RequestBody List<Long> roleIds) {
        log.info("为用户 {} 分配角色: {}", id, roleIds);
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    /**
     * 获取用户角色
     *
     * @param id 用户 ID
     * @return 角色 ID 列表
     */
    @ApiOperation(value = "获取用户角色", notes = "获取用户已分配的角色ID列表")
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoleIds(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        log.info("获取用户角色: {}", id);
        List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }

    /**
     * 修改密码
     *
     * @param id          用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    @ApiOperation(value = "修改密码", notes = "用户修改自己的密码")
    @PostMapping("/{id}/change-password")
    public Result<Void> changePassword(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "新密码", required = true) @RequestParam String newPassword) {
        log.info("修改用户密码: {}", id);
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param id 用户 ID
     * @return 新密码
     */
    @ApiOperation(value = "重置密码", notes = "管理员重置用户密码为默认密码")
    @PostMapping("/{id}/reset-password")
    public Result<String> resetPassword(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        log.info("重置用户密码: {}", id);
        String newPassword = userService.resetPassword(id);
        return Result.success("密码已重置为: " + newPassword, newPassword);
    }
}
