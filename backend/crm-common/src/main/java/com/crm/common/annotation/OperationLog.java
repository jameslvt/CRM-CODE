package com.crm.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在Controller方法上，用于记录操作日志
 *
 * @author CRM System
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 模块名称
     */
    String module() default "";

    /**
     * 操作类型
     */
    String operation() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveParams() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResult() default false;
}
