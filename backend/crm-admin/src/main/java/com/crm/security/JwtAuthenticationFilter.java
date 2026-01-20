package com.crm.security;

import com.crm.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 从请求头中提取 JWT Token，验证并设置用户认证信息到 Spring Security 上下文
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 中存储用户权限的 Key 前缀
     */
    private static final String REDIS_USER_PERMISSION_PREFIX = "user:permission:";

    /**
     * Redis 中存储用户信息的 Key 前缀
     */
    private static final String REDIS_USER_INFO_PREFIX = "user:info:";

    public JwtAuthenticationFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 执行过滤逻辑
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头中获取 Token
        String token = getTokenFromRequest(request);

        // 2. 如果 Token 不存在，直接放行（由 Spring Security 处理未认证的情况）
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. 验证 Token 是否有效
            if (JwtUtils.isTokenExpired(token)) {
                log.warn("Token 已过期");
                filterChain.doFilter(request, response);
                return;
            }

            // 4. 从 Token 中获取用户信息
            Long userId = JwtUtils.getUserIdFromToken(token);
            String username = JwtUtils.getUsernameFromToken(token);

            if (userId == null || username == null) {
                log.warn("Token 中缺少用户信息");
                filterChain.doFilter(request, response);
                return;
            }

            // 5. 检查用户是否已登出（Token 是否在黑名单中）
            String blacklistKey = "token:blacklist:" + token;
            Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token 已被加入黑名单（用户已登出）");
                filterChain.doFilter(request, response);
                return;
            }

            // 6. 从 Redis 中获取用户权限信息
            List<SimpleGrantedAuthority> authorities = getUserAuthorities(userId);

            // 7. 创建认证对象并设置到 Spring Security 上下文
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 8. 刷新 Token 过期时间（可选：延长用户会话）
            refreshTokenExpiration(userId);

            log.debug("用户 [{}] 认证成功，权限: {}", username, authorities);

        } catch (Exception e) {
            log.error("JWT 认证失败: {}", e.getMessage(), e);
            // 认证失败，清除上下文
            SecurityContextHolder.clearContext();
        }

        // 9. 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取 Token
     *
     * @param request HTTP 请求
     * @return JWT Token（去除前缀）
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从 Header 中获取 Authorization
        String header = request.getHeader(JwtUtils.TOKEN_HEADER);
        return JwtUtils.getTokenFromHeader(header);
    }

    /**
     * 从 Redis 中获取用户权限信息
     *
     * @param userId 用户 ID
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> getUserAuthorities(Long userId) {
        String permissionKey = REDIS_USER_PERMISSION_PREFIX + userId;

        try {
            // 从 Redis 中获取用户权限集合
            Set<Object> permissions = redisTemplate.opsForSet().members(permissionKey);

            if (permissions != null && !permissions.isEmpty()) {
                return permissions.stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.toString()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("从 Redis 获取用户权限失败: {}", e.getMessage(), e);
        }

        // 如果 Redis 中没有权限信息，返回空列表
        return new ArrayList<>();
    }

    /**
     * 刷新 Token 过期时间
     * 每次请求时延长用户会话时间（可选功能）
     *
     * @param userId 用户 ID
     */
    private void refreshTokenExpiration(Long userId) {
        try {
            String userInfoKey = REDIS_USER_INFO_PREFIX + userId;
            String permissionKey = REDIS_USER_PERMISSION_PREFIX + userId;

            // 延长用户信息和权限的过期时间（7 天）
            redisTemplate.expire(userInfoKey, 7, TimeUnit.DAYS);
            redisTemplate.expire(permissionKey, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("刷新 Token 过期时间失败: {}", e.getMessage());
        }
    }
}
