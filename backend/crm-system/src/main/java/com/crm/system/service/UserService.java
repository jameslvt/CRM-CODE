package com.crm.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.BusinessException;
import com.crm.system.dto.UserDTO;
import com.crm.system.entity.Department;
import com.crm.system.entity.User;
import com.crm.system.mapper.DepartmentMapper;
import com.crm.system.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * 提供用户管理的业务逻辑
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 创建用户
     *
     * @param userDTO 用户 DTO
     * @return 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserDTO userDTO) {
        log.info("创建用户: {}", userDTO.getUsername());

        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(userDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在: " + userDTO.getUsername());
        }

        // 检查部门是否存在
        if (userDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(userDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("部门不存在");
            }
        }

        User user = new User();
        BeanUtils.copyProperties(userDTO, user);

        // 加密密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        userMapper.insert(user);

        log.info("用户创建成功，ID: {}", user.getId());
        return user.getId();
    }

    /**
     * 更新用户
     *
     * @param userDTO 用户 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDTO userDTO) {
        log.info("更新用户: {}", userDTO.getId());

        User user = userMapper.selectById(userDTO.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户名是否被其他用户使用
        User existUser = userMapper.selectByUsername(userDTO.getUsername());
        if (existUser != null && !existUser.getId().equals(userDTO.getId())) {
            throw new BusinessException("用户名已被使用: " + userDTO.getUsername());
        }

        // 检查部门是否存在
        if (userDTO.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(userDTO.getDepartmentId());
            if (department == null) {
                throw new BusinessException("部门不存在");
            }
        }

        // 保存原密码
        String originalPassword = user.getPassword();

        BeanUtils.copyProperties(userDTO, user);

        // 如果提供了新密码，则加密；否则保持原密码
        if (StringUtils.hasText(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            user.setPassword(originalPassword);
        }

        userMapper.updateById(user);

        log.info("用户更新成功");
    }

    /**
     * 删除用户
     *
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        log.info("删除用户: {}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 删除用户角色关联
        userMapper.deleteUserRolesByUserId(userId);

        // 删除用户
        userMapper.deleteById(userId);

        log.info("用户删除成功");
    }

    /**
     * 根据 ID 查询用户
     *
     * @param userId 用户 ID
     * @return 用户 DTO
     */
    public UserDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(user);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户 DTO
     */
    public UserDTO getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(user);
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
    public IPage<UserDTO> getUserPage(Integer page, Integer size, String username, String realName,
                                      Long departmentId, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.hasText(realName)) {
            queryWrapper.like(User::getRealName, realName);
        }
        if (departmentId != null) {
            queryWrapper.eq(User::getDepartmentId, departmentId);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }

        queryWrapper.orderByDesc(User::getCreateTime);

        IPage<User> userPage = userMapper.selectPage(pageParam, queryWrapper);

        // 转换为 DTO
        Page<UserDTO> dtoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserDTO> dtoList = userPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 为用户分配角色
     *
     * @param userId  用户 ID
     * @param roleIds 角色 ID 列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        log.info("为用户 {} 分配角色: {}", userId, roleIds);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 删除原有角色关联
        userMapper.deleteUserRolesByUserId(userId);

        // 插入新的角色关联
        if (!CollectionUtils.isEmpty(roleIds)) {
            userMapper.insertUserRoles(userId, roleIds);
        }

        log.info("角色分配成功");
    }

    /**
     * 修改密码
     *
     * @param userId      用户 ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("修改用户密码: {}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        log.info("密码修改成功");
    }

    /**
     * 重置密码
     *
     * @param userId 用户 ID
     * @return 新密码
     */
    @Transactional(rollbackFor = Exception.class)
    public String resetPassword(Long userId) {
        log.info("重置用户密码: {}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 重置为默认密码
        String newPassword = "123456";
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        log.info("密码重置成功");
        return newPassword;
    }

    /**
     * 更新最后登录信息
     *
     * @param userId 用户 ID
     * @param ip     登录 IP
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginInfo(Long userId, String ip) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ip);
            userMapper.updateById(user);
        }
    }

    /**
     * 实体转 DTO
     *
     * @param user 用户实体
     * @return 用户 DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);

        // 不返回密码
        dto.setPassword(null);

        // 查询部门名称
        if (user.getDepartmentId() != null) {
            Department department = departmentMapper.selectById(user.getDepartmentId());
            if (department != null) {
                dto.setDepartmentName(department.getDeptName());
            }
        }

        return dto;
    }
}
