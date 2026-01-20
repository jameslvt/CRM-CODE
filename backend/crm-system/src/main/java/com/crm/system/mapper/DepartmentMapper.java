package com.crm.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.system.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper 接口
 * 提供部门数据的持久化操作
 *
 * @author CRM System
 * @since 1.0.0
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 根据部门编码查询部门
     *
     * @param deptCode 部门编码
     * @return 部门实体
     */
    Department selectByDeptCode(@Param("deptCode") String deptCode);

    /**
     * 根据父部门 ID 查询子部门列表
     *
     * @param parentId 父部门 ID
     * @return 部门列表
     */
    List<Department> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有部门（树形结构）
     *
     * @return 部门列表
     */
    List<Department> selectAllTree();

    /**
     * 检查部门是否有子部门
     *
     * @param departmentId 部门 ID
     * @return 子部门数量
     */
    int countChildrenByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 检查部门是否有用户
     *
     * @param departmentId 部门 ID
     * @return 部门下的用户数量
     */
    int countUsersByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据负责人 ID 查询部门列表
     *
     * @param leaderId 负责人 ID
     * @return 部门列表
     */
    List<Department> selectByLeaderId(@Param("leaderId") Long leaderId);
}
