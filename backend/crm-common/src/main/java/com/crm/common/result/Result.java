package com.crm.common.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一响应结果封装类
 * 用于封装所有 API 接口的返回结果，提供统一的响应格式
 *
 * @param <T> 响应数据的类型
 * @author CRM System
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     * 200: 成功
     * 400: 客户端错误（参数错误等）
     * 401: 未认证
     * 403: 无权限
     * 500: 服务器内部错误
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 私有构造函数，防止外部直接实例化
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 私有构造函数，用于内部创建实例
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息和数据）
     *
     * @param msg  响应消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result 对象
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /**
     * 失败响应（默认消息）
     *
     * @param <T> 数据类型
     * @return Result 对象
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    /**
     * 失败响应（自定义消息）
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return Result 对象
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return Result 对象
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败响应（自定义状态码、消息和数据）
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return Result 对象
     */
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }

    /**
     * 判断是否成功
     *
     * @return true: 成功, false: 失败
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
