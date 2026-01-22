package com.crm.common.aspect;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志事件
 * 用于在模块间传递操作日志信息
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class OperationLogEvent {

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String responseResult;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 耗时(毫秒)
     */
    private Long duration;

    /**
     * 状态: 0-失败, 1-成功
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
