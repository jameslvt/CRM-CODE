package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.BusinessException;
import com.crm.system.dto.RoleDTO;
import com.crm.system.entity.Role;
import com.crm.system.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 提供角色管理的业务逻辑
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
public class RoleService {

    @Resource
    private RoleMapper roleMapper;

    /**
     * 创建角色
     *
     * @param roleDTO 角色 DTO
     * @return 角色 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleDTO roleDTO) {
        log.info("创建角色: {}", roleDTO.getRoleName());

        // 检查角色编码是否已存在
        Role existRole = roleMapper.selectByRoleCode(roleDTO.getRoleCode());
        if (existRole != null) {
            throw new BusinessException("角色编码已存在: " + roleDTO.getRoleCode());
        }

        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        roleMapper.insert(role);

        log.info("角色创建成功，ID: {}", role.getId());
        return role.getId();
    }

    /**
     * 更新角色
     *
     * @param roleDTO 角色 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleDTO roleDTO) {
        log.info("更新角色: {}", roleDTO.getId());

        Role role = roleMapper.selectById(roleDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查角色编码是否被其他角色使用
        Role existRole = roleMapper.selectByRoleCode(roleDTO.getRoleCode());
        if (existRole != null && !existRole.getId().equals(roleDTO.getId())) {
            throw new BusinessException("角色编码已被使用: " + roleDTO.getRoleCode());
        }

        BeanUtils.copyProperties(roleDTO, role);
        roleMapper.updateById(role);

        log.info("角色更新成功");
    }

    /**
     * 删除角色
     *
     * @param roleId 角色 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        log.info("删除角色: {}", roleId);

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否被用户使用
        int userCount = roleMapper.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException("该角色已被用户使用，无法删除");
        }

        // 删除角色权限关联
        roleMapper.deleteRolePermissionsByRoleId(roleId);

        // 删除角色
        roleMapper.deleteById(roleId);

        log.info("角色删除成功");
    }

    /**
     * 根据 ID 查询角色
     *
     * @param roleId 角色 ID
     * @return 角色 DTO
     */
    public RoleDTO getRoleById(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return convertToDTO(role);
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
    public IPage<RoleDTO> getRolePage(Integer page, Integer size, String roleName, Integer status) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(roleName)) {
            queryWrapper.like(Role::getRoleName, roleName);
        }
        if (status != null) {
            queryWrapper.eq(Role::getStatus, status);
        }

        queryWrapper.orderByAsc(Role::getSort);

        IPage<Role> rolePage = roleMapper.selectPage(pageParam, queryWrapper);

        // 转换为 DTO
        Page<RoleDTO> dtoPage = new Page<>(rolePage.getCurrent(), rolePage.getSize(), rolePage.getTotal());
        List<RoleDTO> dtoList = rolePage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getStatus, 1)
                        .orderByAsc(Role::getSort)
        );

        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 为角色分配权限
     *
     * @param roleId        角色 ID
     * @param permissionIds 权限 ID 列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        log.info("为角色 {} 分配权限: {}", roleId, permissionIds);

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 删除原有权限关联
        roleMapper.deleteRolePermissionsByRoleId(roleId);

        // 插入新的权限关联
        if (!CollectionUtils.isEmpty(permissionIds)) {
            roleMapper.insertRolePermissions(roleId, permissionIds);
        }

        log.info("权限分配成功");
    }

    /**
     * 查询角色的权限 ID 列表
     *
     * @param roleId 角色 ID
     * @return 权限 ID 列表
     */
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return roleMapper.selectPermissionIdsByRoleId(roleId);
    }

    /**
     * 根据用户 ID 查询角色列表
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    public List<RoleDTO> getRolesByUserId(Long userId) {
        List<Role> roles = roleMapper.selectByUserId(userId);
        return roles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 实体转 DTO
     *
     * @param role 角色实体
     * @return 角色 DTO
     */
    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleCode(role.getRoleKey());
        dto.setSortOrder(role.getSort());
        dto.setStatus(role.getStatus());
        dto.setRemark(role.getRemark());
        dto.setCreateTime(role.getCreateTime());
        dto.setUpdateTime(role.getUpdateTime());
        return dto;
    }
}
