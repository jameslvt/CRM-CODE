package com.crm.system.controller;

import com.crm.common.result.Result;
import com.crm.common.utils.JwtUtils;
import com.crm.system.dto.LoginRequest;
import com.crm.system.dto.LoginResponse;
import com.crm.system.dto.UserDTO;
import com.crm.system.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 认证控制器
 * 提供用户登录、登出、Token 刷新等接口
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param request      HTTP 请求
     * @return 登录响应
     */
    @ApiOperation(value = "用户登录", notes = "用户通过用户名和密码登录系统")
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        log.info("用户登录请求: {}", loginRequest.getUsername());

        // 获取客户端 IP
        String ip = getClientIp(request);

        LoginResponse response = authService.login(loginRequest, ip);
        return Result.success(response);
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     * @return 操作结果
     */
    @ApiOperation(value = "用户登出", notes = "用户退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        log.info("用户登出请求");

        // 从 Header 中获取 Token
        String token = getTokenFromRequest(request);
        if (token != null) {
            Long userId = JwtUtils.getUserIdFromToken(token);
            if (userId != null) {
                authService.logout(userId);
            }
        }

        return Result.success();
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @ApiOperation(value = "刷新Token", notes = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh")
    public Result<String> refreshToken(
            @ApiParam(value = "刷新令牌", required = true)
            @RequestParam String refreshToken) {
        log.info("刷新 Token 请求");

        String newAccessToken = authService.refreshToken(refreshToken);
        return Result.success(newAccessToken);
    }

    /**
     * 获取当前用户信息
     *
     * @param request HTTP 请求
     * @return 用户信息
     */
    @ApiOperation(value = "获取当前用户信息", notes = "根据 Token 获取当前登录用户的信息")
    @GetMapping("/current-user")
    public Result<UserDTO> getCurrentUser(HttpServletRequest request) {
        log.info("获取当前用户信息");

        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error(401, "未登录");
        }

        UserDTO userDTO = authService.getUserByToken(token);
        return Result.success(userDTO);
    }

    /**
     * 获取当前用户权限列表
     *
     * @param request HTTP 请求
     * @return 权限列表
     */
    @ApiOperation(value = "获取当前用户权限", notes = "根据 Token 获取当前用户的权限列表")
    @GetMapping("/current-permissions")
    public Result<List<String>> getCurrentPermissions(HttpServletRequest request) {
        log.info("获取当前用户权限");

        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error(401, "未登录");
        }

        List<String> permissions = authService.getPermissionsByToken(token);
        return Result.success(permissions);
    }

    /**
     * 获取当前用户角色列表
     *
     * @param request HTTP 请求
     * @return 角色列表
     */
    @ApiOperation(value = "获取当前用户角色", notes = "根据 Token 获取当前用户的角色列表")
    @GetMapping("/current-roles")
    public Result<List<String>> getCurrentRoles(HttpServletRequest request) {
        log.info("获取当前用户角色");

        String token = getTokenFromRequest(request);
        if (token == null) {
            return Result.error(401, "未登录");
        }

        List<String> roles = authService.getRolesByToken(token);
        return Result.success(roles);
    }

    /**
     * 从请求中获取 Token
     *
     * @param request HTTP 请求
     * @return Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(JwtUtils.TOKEN_HEADER);
        return JwtUtils.getTokenFromHeader(header);
    }

    /**
     * 获取客户端 IP 地址
     *
     * @param request HTTP 请求
     * @return IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
