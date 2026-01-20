package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.BusinessException;
import com.crm.system.dto.DepartmentDTO;
import com.crm.system.entity.Department;
import com.crm.system.mapper.DepartmentMapper;
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
 * 部门服务实现类
 * 提供部门管理的业务逻辑
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
public class DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 创建部门
     *
     * @param departmentDTO 部门 DTO
     * @return 部门 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentDTO departmentDTO) {
        log.info("创建部门: {}", departmentDTO.getDeptName());

        // 检查部门编码是否已存在
        Department existDepartment = departmentMapper.selectByDeptCode(departmentDTO.getDeptCode());
        if (existDepartment != null) {
            throw new BusinessException("部门编码已存在: " + departmentDTO.getDeptCode());
        }

        // 如果有父部门，检查父部门是否存在
        if (departmentDTO.getParentId() != null && departmentDTO.getParentId() > 0) {
            Department parentDepartment = departmentMapper.selectById(departmentDTO.getParentId());
            if (parentDepartment == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        departmentMapper.insert(department);

        log.info("部门创建成功，ID: {}", department.getId());
        return department.getId();
    }

    /**
     * 更新部门
     *
     * @param departmentDTO 部门 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(DepartmentDTO departmentDTO) {
        log.info("更新部门: {}", departmentDTO.getId());

        Department department = departmentMapper.selectById(departmentDTO.getId());
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查部门编码是否被其他部门使用
        Department existDepartment = departmentMapper.selectByDeptCode(departmentDTO.getDeptCode());
        if (existDepartment != null && !existDepartment.getId().equals(departmentDTO.getId())) {
            throw new BusinessException("部门编码已被使用: " + departmentDTO.getDeptCode());
        }

        // 不能将自己设置为父部门
        if (departmentDTO.getParentId() != null && departmentDTO.getParentId().equals(departmentDTO.getId())) {
            throw new BusinessException("不能将自己设置为父部门");
        }

        BeanUtils.copyProperties(departmentDTO, department);
        departmentMapper.updateById(department);

        log.info("部门更新成功");
    }

    /**
     * 删除部门
     *
     * @param departmentId 部门 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long departmentId) {
        log.info("删除部门: {}", departmentId);

        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查是否有子部门
        int childrenCount = departmentMapper.countChildrenByDepartmentId(departmentId);
        if (childrenCount > 0) {
            throw new BusinessException("该部门下存在子部门，无法删除");
        }

        // 检查是否有用户
        int userCount = departmentMapper.countUsersByDepartmentId(departmentId);
        if (userCount > 0) {
            throw new BusinessException("该部门下存在用户，无法删除");
        }

        departmentMapper.deleteById(departmentId);
        log.info("部门删除成功");
    }

    /**
     * 根据 ID 查询部门
     *
     * @param departmentId 部门 ID
     * @return 部门 DTO
     */
    public DepartmentDTO getDepartmentById(Long departmentId) {
        Department department = departmentMapper.selectById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }
        return convertToDTO(department);
    }

    /**
     * 分页查询部门列表
     *
     * @param page     页码
     * @param size     每页大小
     * @param deptName 部门名称（模糊查询）
     * @param status   状态
     * @return 分页结果
     */
    public IPage<DepartmentDTO> getDepartmentPage(Integer page, Integer size, String deptName, Integer status) {
        Page<Department> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(deptName)) {
            queryWrapper.like(Department::getDeptName, deptName);
        }
        if (status != null) {
            queryWrapper.eq(Department::getStatus, status);
        }

        queryWrapper.orderByAsc(Department::getSortOrder);

        IPage<Department> departmentPage = departmentMapper.selectPage(pageParam, queryWrapper);

        // 转换为 DTO
        Page<DepartmentDTO> dtoPage = new Page<>(departmentPage.getCurrent(), departmentPage.getSize(), departmentPage.getTotal());
        List<DepartmentDTO> dtoList = departmentPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 查询部门树
     *
     * @return 部门树列表
     */
    public List<DepartmentDTO> getDepartmentTree() {
        List<Department> allDepartments = departmentMapper.selectList(
                new LambdaQueryWrapper<Department>()
                        .eq(Department::getStatus, 1)
                        .orderByAsc(Department::getSortOrder)
        );

        return buildDepartmentTree(allDepartments, 0L);
    }

    /**
     * 根据父部门 ID 查询子部门列表
     *
     * @param parentId 父部门 ID
     * @return 部门列表
     */
    public List<DepartmentDTO> getDepartmentsByParentId(Long parentId) {
        List<Department> departments = departmentMapper.selectByParentId(parentId);
        return departments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 构建部门树
     *
     * @param departments 部门列表
     * @param parentId    父部门 ID
     * @return 部门树
     */
    private List<DepartmentDTO> buildDepartmentTree(List<Department> departments, Long parentId) {
        List<DepartmentDTO> tree = new ArrayList<>();

        for (Department department : departments) {
            if (department.getParentId().equals(parentId)) {
                DepartmentDTO dto = convertToDTO(department);
                dto.setChildren(buildDepartmentTree(departments, department.getId()));
                tree.add(dto);
            }
        }

        return tree;
    }

    /**
     * 实体转 DTO
     *
     * @param department 部门实体
     * @return 部门 DTO
     */
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        BeanUtils.copyProperties(department, dto);
        return dto;
    }
}
