package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 * 提供用户数据的持久化操作
 * 使用 MyBatis Plus BaseMapper 提供的基础 CRUD 方法
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 删除用户角色关联
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteUserRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户角色ID列表
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    java.util.List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 添加用户角色关联
     *
     * @param userId 用户 ID
     * @param roleId 角色 ID
     * @return 影响行数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
