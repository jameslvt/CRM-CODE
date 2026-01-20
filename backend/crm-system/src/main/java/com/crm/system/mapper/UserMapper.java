package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 * 提供用户数据的持久化操作
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据用户 ID 查询用户的角色编码列表
     *
     * @param userId 用户 ID
     * @return 角色编码列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 查询用户的权限编码列表
     *
     * @param userId 用户 ID
     * @return 权限编码列表
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据部门 ID 查询用户列表
     *
     * @param departmentId 部门 ID
     * @return 用户列表
     */
    List<User> selectByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 批量删除用户角色关联
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteUserRolesByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关联
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 列表
     * @return 影响行数
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}
