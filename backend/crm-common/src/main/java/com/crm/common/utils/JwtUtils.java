package com.crm.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和解析 JWT Token
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
public class JwtUtils {

    /**
     * JWT 密钥（生产环境应从配置文件读取）
     */
    private static final String SECRET_KEY = "crm-system-secret-key-2024-please-change-in-production";

    /**
     * Token 有效期（默认 7 天，单位：毫秒）
     */
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Token Header 名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 用户 ID 的 Claim Key
     */
    private static final String CLAIM_KEY_USER_ID = "userId";

    /**
     * 用户名的 Claim Key
     */
    private static final String CLAIM_KEY_USERNAME = "username";

    /**
     * 创建时间的 Claim Key
     */
    private static final String CLAIM_KEY_CREATED = "created";

    /**
     * 生成 JWT Token
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @return JWT Token
     */
    public static String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 生成 JWT Token（带自定义 Claims）
     *
     * @param claims 自定义声明
     * @return JWT Token
     */
    public static String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 从 Token 中获取 Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析 Token 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        Object userId = claims.get(CLAIM_KEY_USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? (String) claims.get(CLAIM_KEY_USERNAME) : null;
    }

    /**
     * 从 Token 中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.getExpiration() : null;
    }

    /**
     * 判断 Token 是否过期
     *
     * @param token JWT Token
     * @return true: 已过期, false: 未过期
     */
    public static boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate != null && expirationDate.before(new Date());
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token    JWT Token
     * @param userId   用户 ID
     * @param username 用户名
     * @return true: 有效, false: 无效
     */
    public static boolean validateToken(String token, Long userId, String username) {
        Long tokenUserId = getUserIdFromToken(token);
        String tokenUsername = getUsernameFromToken(token);
        return tokenUserId != null
                && tokenUserId.equals(userId)
                && tokenUsername != null
                && tokenUsername.equals(username)
                && !isTokenExpired(token);
    }

    /**
     * 刷新 Token
     *
     * @param token 旧的 JWT Token
     * @return 新的 JWT Token
     */
    public static String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从 Header 中获取 Token
     *
     * @param header Authorization Header 值
     * @return JWT Token（去除前缀）
     */
    public static String getTokenFromHeader(String header) {
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
