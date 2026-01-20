package com.crm.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验服务
 * 基于 RBAC (Role-Based Access Control) 模型进行权限校验
 * 支持在 Controller 方法上使用 @PreAuthorize("@permissionService.hasPermission('system:user:add')") 进行权限控制
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service("permissionService")
public class PermissionService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 中存储用户权限的 Key 前缀
     */
    private static final String REDIS_USER_PERMISSION_PREFIX = "user:permission:";

    /**
     * 超级管理员角色标识
     */
    private static final String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    public PermissionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 判断当前用户是否拥有指定权限
     * 使用方式: @PreAuthorize("@permissionService.hasPermission('system:user:add')")
     *
     * @param permission 权限标识（如: system:user:add）
     * @return true: 有权限, false: 无权限
     */
    public boolean hasPermission(String permission) {
        // 1. 获取当前用户认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("用户未认证，权限校验失败");
            return false;
        }

        // 2. 获取当前用户 ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            log.warn("无法获取当前用户 ID，权限校验失败");
            return false;
        }

        // 3. 检查是否为超级管理员（超级管理员拥有所有权限）
        if (isSuperAdmin(authentication)) {
            log.debug("用户 [{}] 是超级管理员，拥有所有权限", userId);
            return true;
        }

        // 4. 从 Redis 中获取用户权限列表
        Set<String> userPermissions = getUserPermissions(userId);
        if (CollectionUtils.isEmpty(userPermissions)) {
            log.warn("用户 [{}] 没有任何权限", userId);
            return false;
        }

        // 5. 判断用户是否拥有指定权限
        boolean hasPermission = userPermissions.contains(permission);
        if (hasPermission) {
            log.debug("用户 [{}] 拥有权限 [{}]", userId, permission);
        } else {
            log.warn("用户 [{}] 没有权限 [{}]", userId, permission);
        }

        return hasPermission;
    }

    /**
     * 判断当前用户是否拥有任意一个指定权限
     * 使用方式: @PreAuthorize("@permissionService.hasAnyPermission('system:user:add', 'system:user:edit')")
     *
     * @param permissions 权限标识数组
     * @return true: 拥有任意一个权限, false: 没有任何权限
     */
    public boolean hasAnyPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        for (String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前用户是否拥有所有指定权限
     * 使用方式: @PreAuthorize("@permissionService.hasAllPermissions('system:user:add', 'system:user:edit')")
     *
     * @param permissions 权限标识数组
     * @return true: 拥有所有权限, false: 缺少任意一个权限
     */
    public boolean hasAllPermissions(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        for (String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断当前用户是否拥有指定角色
     * 使用方式: @PreAuthorize("@permissionService.hasRole('ADMIN')")
     *
     * @param role 角色标识（如: ADMIN）
     * @return true: 拥有角色, false: 没有角色
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 角色需要加上 ROLE_ 前缀
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }

        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
    }

    /**
     * 判断当前用户是否拥有任意一个指定角色
     * 使用方式: @PreAuthorize("@permissionService.hasAnyRole('ADMIN', 'MANAGER')")
     *
     * @param roles 角色标识数组
     * @return true: 拥有任意一个角色, false: 没有任何角色
     */
    public boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }

        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断当前用户是否为超级管理员
     *
     * @return true: 是超级管理员, false: 不是超级管理员
     */
    public boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isSuperAdmin(authentication);
    }

    /**
     * 判断用户是否为超级管理员
     *
     * @param authentication 认证信息
     * @return true: 是超级管理员, false: 不是超级管理员
     */
    private boolean isSuperAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtils.isEmpty(authorities)) {
            return false;
        }

        return authorities.stream()
                .anyMatch(authority -> SUPER_ADMIN_ROLE.equals(authority.getAuthority()));
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }

        return null;
    }

    /**
     * 从 Redis 中获取用户权限列表
     *
     * @param userId 用户 ID
     * @return 权限集合
     */
    private Set<String> getUserPermissions(Long userId) {
        String permissionKey = REDIS_USER_PERMISSION_PREFIX + userId;

        try {
            Set<Object> permissions = redisTemplate.opsForSet().members(permissionKey);
            if (permissions != null && !permissions.isEmpty()) {
                return permissions.stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            log.error("从 Redis 获取用户权限失败: {}", e.getMessage(), e);
        }

        return null;
    }
}
