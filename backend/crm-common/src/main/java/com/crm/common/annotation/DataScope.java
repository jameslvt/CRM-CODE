package com.crm.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 标注在Mapper方法上，用于实现数据权限过滤
 *
 * @author CRM System
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门表的别名
     */
    String deptAlias() default "";

    /**
     * 用户表的别名
     */
    String userAlias() default "";

    /**
     * 负责人字段名（owner_id）
     */
    String ownerField() default "owner_id";

    /**
     * 部门字段名（dept_id）
     */
    String deptField() default "dept_id";
}
