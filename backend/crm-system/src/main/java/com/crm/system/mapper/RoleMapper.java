package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色 Mapper 接口
 * 提供角色数据的持久化操作
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色实体
     */
    Role selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据用户 ID 查询角色列表
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 查询权限 ID 列表
     *
     * @param roleId 角色 ID
     * @return 权限 ID 列表
     */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除角色权限关联
     *
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int deleteRolePermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     *
     * @param roleId        角色 ID
     * @param permissionIds 权限 ID 列表
     * @return 影响行数
     */
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    /**
     * 检查角色是否被用户使用
     *
     * @param roleId 角色 ID
     * @return 使用该角色的用户数量
     */
    int countUsersByRoleId(@Param("roleId") Long roleId);
}
