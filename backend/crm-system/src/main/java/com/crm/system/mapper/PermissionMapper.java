package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限 Mapper 接口
 * 提供权限数据的持久化操作
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限编码查询权限
     *
     * @param permissionCode 权限编码
     * @return 权限实体
     */
    Permission selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根据用户 ID 查询权限列表
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    List<Permission> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 查询权限列表
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据父权限 ID 查询子权限列表
     *
     * @param parentId 父权限 ID
     * @return 权限列表
     */
    List<Permission> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有权限（树形结构）
     *
     * @return 权限列表
     */
    List<Permission> selectAllTree();

    /**
     * 检查权限是否被角色使用
     *
     * @param permissionId 权限 ID
     * @return 使用该权限的角色数量
     */
    int countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查权限是否有子权限
     *
     * @param permissionId 权限 ID
     * @return 子权限数量
     */
    int countChildrenByPermissionId(@Param("permissionId") Long permissionId);
}
