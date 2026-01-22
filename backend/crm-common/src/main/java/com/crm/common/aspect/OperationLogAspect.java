package com.crm.common.aspect;

import com.crm.common.annotation.OperationLog;
import com.crm.common.utils.IpUtils;
import com.crm.common.utils.JsonUtils;
import com.crm.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 * 拦截带有 @OperationLog 注解的方法，记录操作日志
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 环绕通知，记录操作日志
     */
    @Around("@annotation(com.crm.common.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog annotation = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 构建日志事件对象
        OperationLogEvent logEvent = new OperationLogEvent();
        logEvent.setModule(annotation.module());
        logEvent.setOperation(annotation.operation());
        logEvent.setMethod(request != null ? request.getMethod() : "");
        logEvent.setRequestUrl(request != null ? request.getRequestURI() : "");
        logEvent.setIp(request != null ? IpUtils.getClientIp(request) : "");
        logEvent.setCreateTime(LocalDateTime.now());

        // 获取当前用户信息
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String username = SecurityUtils.getCurrentUsername();
            logEvent.setUserId(userId);
            logEvent.setUsername(username);
        } catch (Exception e) {
            // 未登录状态，忽略
        }

        // 保存请求参数
        if (annotation.saveParams()) {
            String params = getRequestParams(joinPoint, signature);
            // 限制参数长度，避免过长
            if (params != null && params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
            logEvent.setRequestParams(params);
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            logEvent.setStatus(1);

            // 保存响应结果
            if (annotation.saveResult() && result != null) {
                String resultJson = JsonUtils.toJson(result);
                if (resultJson != null && resultJson.length() > 2000) {
                    resultJson = resultJson.substring(0, 2000) + "...";
                }
                logEvent.setResponseResult(resultJson);
            }

            return result;
        } catch (Throwable e) {
            logEvent.setStatus(0);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 500) {
                errorMsg = errorMsg.substring(0, 500) + "...";
            }
            logEvent.setErrorMsg(errorMsg);
            throw e;
        } finally {
            // 计算耗时
            logEvent.setDuration(System.currentTimeMillis() - startTime);

            // 发布日志事件
            try {
                eventPublisher.publishEvent(logEvent);
            } catch (Exception e) {
                log.error("发布操作日志事件失败", e);
            }
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        try {
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            if (paramNames == null || args == null || paramNames.length == 0) {
                return null;
            }

            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < paramNames.length; i++) {
                Object arg = args[i];
                // 过滤不需要记录的参数类型
                if (arg instanceof HttpServletRequest
                        || arg instanceof HttpServletResponse
                        || arg instanceof MultipartFile) {
                    continue;
                }
                // 过滤敏感参数
                String paramName = paramNames[i].toLowerCase();
                if (paramName.contains("password") || paramName.contains("secret") || paramName.contains("token")) {
                    params.put(paramNames[i], "******");
                } else {
                    params.put(paramNames[i], arg);
                }
            }

            return JsonUtils.toJson(params);
        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            return null;
        }
    }
}
