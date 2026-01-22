package com.crm.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 * 记录系统中的所有操作行为，用于审计和问题追踪
 *
 * @author CRM System
 * @since 1.0.0
 */
@Data
@TableName("crm_operation_log")
public class OperationLog {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作类型: 查询、新增、修改、删除、导入、导出等
     */
    private String operation;

    /**
     * 请求方法: GET、POST、PUT、DELETE
     */
    private String method;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数 (JSON格式)
     */
    private String requestParams;

    /**
     * 响应结果 (JSON格式)
     */
    private String responseResult;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 耗时 (毫秒)
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
