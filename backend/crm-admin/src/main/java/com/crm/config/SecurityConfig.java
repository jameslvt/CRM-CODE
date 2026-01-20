package com.crm.config;

import com.crm.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 * 配置 JWT 认证、权限控制、接口放行等安全策略
 *
 * @author CRM System
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 配置密码编码器
     * 使用 BCrypt 加密算法
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 AuthenticationManager
     * 用于用户认证
     *
     * @return AuthenticationManager
     * @throws Exception 配置异常
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置 HTTP 安全策略
     *
     * @param http HttpSecurity
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（使用 JWT 不需要 CSRF 保护）
                .csrf().disable()

                // 禁用 Session（使用 JWT 无状态认证）
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 配置请求授权规则
                .authorizeRequests()
                // 放行登录接口
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/register").permitAll()
                .antMatchers("/api/auth/captcha").permitAll()
                .antMatchers("/api/auth/refresh").permitAll()

                // 放行 Swagger 文档接口
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/v3/api-docs").permitAll()
                .antMatchers("/webjars/**").permitAll()

                // 放行静态资源
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/error").permitAll()

                // 放行健康检查接口
                .antMatchers("/actuator/**").permitAll()

                // 其他所有请求都需要认证
                .anyRequest().authenticated()
                .and()

                // 添加 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 禁用默认的登出功能（使用自定义登出接口）
                .logout().disable()

                // 配置异常处理
                .exceptionHandling()
                // 未认证时的处理
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"code\":401,\"msg\":\"未认证，请先登录\",\"data\":null}");
                })
                // 无权限时的处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write("{\"code\":403,\"msg\":\"无权限访问\",\"data\":null}");
                });

        // 禁用缓存
        http.headers().cacheControl();
    }

    /**
     * 配置认证管理器
     * 可以在这里配置 UserDetailsService 和 PasswordEncoder
     *
     * @param auth AuthenticationManagerBuilder
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 这里可以配置自定义的 UserDetailsService
        // auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
