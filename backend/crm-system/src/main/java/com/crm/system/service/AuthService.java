package com.crm.system.service;

import com.crm.common.exception.BusinessException;
import com.crm.common.utils.JwtUtils;
import com.crm.system.dto.LoginRequest;
import com.crm.system.dto.LoginResponse;
import com.crm.system.dto.UserDTO;
import com.crm.system.entity.User;
import com.crm.system.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 * 提供用户登录、登出、Token 刷新等功能
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Service
public class AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Token 在 Redis 中的前缀
     */
    private static final String TOKEN_PREFIX = "crm:token:";

    /**
     * Token 过期时间（7 天，单位：秒）
     */
    private static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param ip           登录 IP
     * @return 登录响应
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest loginRequest, String ip) {
        log.info("用户登录: {}", loginRequest.getUsername());

        // 查询用户
        User user = userMapper.selectByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成 Token
        String accessToken = JwtUtils.generateToken(user.getId(), user.getUsername());
        String refreshToken = JwtUtils.generateToken(user.getId(), user.getUsername());

        // 将 Token 存入 Redis
        String tokenKey = TOKEN_PREFIX + user.getId();
        redisTemplate.opsForValue().set(tokenKey, accessToken, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        // 更新最后登录信息
        userService.updateLastLoginInfo(user.getId(), ip);

        // 查询用户权限和角色
        List<String> permissions = userMapper.selectPermissionCodesByUserId(user.getId());
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRE_TIME);

        // 设置用户信息
        UserDTO userDTO = userService.getUserById(user.getId());
        response.setUserInfo(userDTO);
        response.setPermissions(permissions);
        response.setRoles(roles);

        log.info("用户登录成功: {}", loginRequest.getUsername());
        return response;
    }

    /**
     * 用户登出
     *
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void logout(Long userId) {
        log.info("用户登出: {}", userId);

        // 从 Redis 中删除 Token
        String tokenKey = TOKEN_PREFIX + userId;
        redisTemplate.delete(tokenKey);

        log.info("用户登出成功");
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public String refreshToken(String refreshToken) {
        log.info("刷新 Token");

        // 验证 Token
        if (JwtUtils.isTokenExpired(refreshToken)) {
            throw new BusinessException("刷新令牌已过期");
        }

        // 获取用户信息
        Long userId = JwtUtils.getUserIdFromToken(refreshToken);
        String username = JwtUtils.getUsernameFromToken(refreshToken);

        if (userId == null || username == null) {
            throw new BusinessException("无效的刷新令牌");
        }

        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        // 生成新的 Token
        String newAccessToken = JwtUtils.generateToken(userId, username);

        // 更新 Redis 中的 Token
        String tokenKey = TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(tokenKey, newAccessToken, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);

        log.info("Token 刷新成功");
        return newAccessToken;
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token 访问令牌
     * @return true: 有效, false: 无效
     */
    public boolean validateToken(String token) {
        try {
            // 验证 Token 是否过期
            if (JwtUtils.isTokenExpired(token)) {
                return false;
            }

            // 获取用户信息
            Long userId = JwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return false;
            }

            // 检查 Redis 中是否存在该 Token
            String tokenKey = TOKEN_PREFIX + userId;
            Object cachedToken = redisTemplate.opsForValue().get(tokenKey);

            return cachedToken != null && cachedToken.equals(token);
        } catch (Exception e) {
            log.error("Token 验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据 Token 获取用户信息
     *
     * @param token 访问令牌
     * @return 用户 DTO
     */
    public UserDTO getUserByToken(String token) {
        Long userId = JwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException("无效的令牌");
        }

        return userService.getUserById(userId);
    }

    /**
     * 根据 Token 获取用户权限列表
     *
     * @param token 访问令牌
     * @return 权限编码列表
     */
    public List<String> getPermissionsByToken(String token) {
        Long userId = JwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException("无效的令牌");
        }

        return userMapper.selectPermissionCodesByUserId(userId);
    }

    /**
     * 根据 Token 获取用户角色列表
     *
     * @param token 访问令牌
     * @return 角色编码列表
     */
    public List<String> getRolesByToken(String token) {
        Long userId = JwtUtils.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException("无效的令牌");
        }

        return userMapper.selectRoleCodesByUserId(userId);
    }
}
