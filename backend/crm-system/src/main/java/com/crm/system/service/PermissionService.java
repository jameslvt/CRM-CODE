package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.BusinessException;
import com.crm.system.dto.PermissionDTO;
import com.crm.system.entity.Permission;
import com.crm.system.mapper.PermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现类
 * 提供权限管理的业务逻辑（CRUD 操作）
 * 注意：此服务与 com.crm.security.PermissionService（权限校验服务）不同
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service("permissionManageService")
public class PermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 创建权限
     *
     * @param permissionDTO 权限 DTO
     * @return 权限 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createPermission(PermissionDTO permissionDTO) {
        log.info("创建权限: {}", permissionDTO.getPermissionName());

        // 检查权限编码是否已存在
        Permission existPermission = permissionMapper.selectByPermissionCode(permissionDTO.getPermissionCode());
        if (existPermission != null) {
            throw new BusinessException("权限编码已存在: " + permissionDTO.getPermissionCode());
        }

        // 如果有父权限，检查父权限是否存在
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId() > 0) {
            Permission parentPermission = permissionMapper.selectById(permissionDTO.getParentId());
            if (parentPermission == null) {
                throw new BusinessException("父权限不存在");
            }
        }

        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        permissionMapper.insert(permission);

        log.info("权限创建成功，ID: {}", permission.getId());
        return permission.getId();
    }

    /**
     * 更新权限
     *
     * @param permissionDTO 权限 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(PermissionDTO permissionDTO) {
        log.info("更新权限: {}", permissionDTO.getId());

        Permission permission = permissionMapper.selectById(permissionDTO.getId());
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查权限编码是否被其他权限使用
        Permission existPermission = permissionMapper.selectByPermissionCode(permissionDTO.getPermissionCode());
        if (existPermission != null && !existPermission.getId().equals(permissionDTO.getId())) {
            throw new BusinessException("权限编码已被使用: " + permissionDTO.getPermissionCode());
        }

        // 不能将自己设置为父权限
        if (permissionDTO.getParentId() != null && permissionDTO.getParentId().equals(permissionDTO.getId())) {
            throw new BusinessException("不能将自己设置为父权限");
        }

        BeanUtils.copyProperties(permissionDTO, permission);
        permissionMapper.updateById(permission);

        log.info("权限更新成功");
    }

    /**
     * 删除权限
     *
     * @param permissionId 权限 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long permissionId) {
        log.info("删除权限: {}", permissionId);

        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }

        // 检查是否有子权限
        int childrenCount = permissionMapper.countChildrenByPermissionId(permissionId);
        if (childrenCount > 0) {
            throw new BusinessException("该权限下存在子权限，无法删除");
        }

        // 检查是否被角色使用
        int roleCount = permissionMapper.countRolesByPermissionId(permissionId);
        if (roleCount > 0) {
            throw new BusinessException("该权限已被角色使用，无法删除");
        }

        permissionMapper.deleteById(permissionId);
        log.info("权限删除成功");
    }

    /**
     * 根据 ID 查询权限
     *
     * @param permissionId 权限 ID
     * @return 权限 DTO
     */
    public PermissionDTO getPermissionById(Long permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        return convertToDTO(permission);
    }

    /**
     * 分页查询权限列表
     *
     * @param page             页码
     * @param size             每页大小
     * @param permissionName   权限名称（模糊查询）
     * @param permissionType   权限类型
     * @param status           状态
     * @return 分页结果
     */
    public IPage<PermissionDTO> getPermissionPage(Integer page, Integer size, String permissionName,
                                                   Integer permissionType, Integer status) {
        Page<Permission> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(permissionName)) {
            queryWrapper.like(Permission::getPermissionName, permissionName);
        }
        if (permissionType != null) {
            queryWrapper.eq(Permission::getPermissionType, permissionType);
        }
        if (status != null) {
            queryWrapper.eq(Permission::getStatus, status);
        }

        queryWrapper.orderByAsc(Permission::getSortOrder);

        IPage<Permission> permissionPage = permissionMapper.selectPage(pageParam, queryWrapper);

        // 转换为 DTO
        Page<PermissionDTO> dtoPage = new Page<>(permissionPage.getCurrent(), permissionPage.getSize(), permissionPage.getTotal());
        List<PermissionDTO> dtoList = permissionPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 查询权限树
     *
     * @return 权限树列表
     */
    public List<PermissionDTO> getPermissionTree() {
        List<Permission> allPermissions = permissionMapper.selectList(
                new LambdaQueryWrapper<Permission>()
                        .eq(Permission::getStatus, 1)
                        .orderByAsc(Permission::getSortOrder)
        );

        return buildPermissionTree(allPermissions, 0L);
    }

    /**
     * 根据角色 ID 查询权限列表
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    public List<PermissionDTO> getPermissionsByRoleId(Long roleId) {
        List<Permission> permissions = permissionMapper.selectByRoleId(roleId);
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户 ID 查询权限列表
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    public List<PermissionDTO> getPermissionsByUserId(Long userId) {
        List<Permission> permissions = permissionMapper.selectByUserId(userId);
        return permissions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 构建权限树
     *
     * @param permissions 权限列表
     * @param parentId    父权限 ID
     * @return 权限树
     */
    private List<PermissionDTO> buildPermissionTree(List<Permission> permissions, Long parentId) {
        List<PermissionDTO> tree = new ArrayList<>();

        for (Permission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                PermissionDTO dto = convertToDTO(permission);
                dto.setChildren(buildPermissionTree(permissions, permission.getId()));
                tree.add(dto);
            }
        }

        return tree;
    }

    /**
     * 实体转 DTO
     *
     * @param permission 权限实体
     * @return 权限 DTO
     */
    private PermissionDTO convertToDTO(Permission permission) {
        PermissionDTO dto = new PermissionDTO();
        BeanUtils.copyProperties(permission, dto);
        return dto;
    }
}
