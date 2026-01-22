package com.crm.common.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.crm.common.annotation.DataScope;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 数据权限拦截器
 * 基于MyBatis Plus拦截器实现数据权限过滤
 *
 * 数据权限范围:
 * 1 - 全部数据权限
 * 2 - 本部门及下级部门数据权限
 * 3 - 本部门数据权限
 * 4 - 仅本人数据权限
 *
 * @author CRM System
 * @since 1.0.0
 */
@Slf4j
@Component
public class DataScopeInterceptor implements InnerInterceptor {

    /**
     * 数据权限范围常量
     */
    public static final int DATA_SCOPE_ALL = 1;
    public static final int DATA_SCOPE_DEPT_AND_CHILD = 2;
    public static final int DATA_SCOPE_DEPT = 3;
    public static final int DATA_SCOPE_SELF = 4;

    /**
     * 线程本地变量，存储当前用户的数据权限信息
     */
    private static final ThreadLocal<DataScopeContext> DATA_SCOPE_CONTEXT = new ThreadLocal<>();

    /**
     * 设置数据权限上下文
     */
    public static void setDataScopeContext(DataScopeContext context) {
        DATA_SCOPE_CONTEXT.set(context);
    }

    /**
     * 清除数据权限上下文
     */
    public static void clearDataScopeContext() {
        DATA_SCOPE_CONTEXT.remove();
    }

    /**
     * 获取数据权限上下文
     */
    public static DataScopeContext getDataScopeContext() {
        return DATA_SCOPE_CONTEXT.get();
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {

        // 获取数据权限上下文
        DataScopeContext context = DATA_SCOPE_CONTEXT.get();
        if (context == null) {
            // 尝试从当前用户获取数据权限
            context = buildContextFromCurrentUser();
        }

        if (context == null || context.getDataScope() == DATA_SCOPE_ALL) {
            // 全部数据权限，不做过滤
            return;
        }

        // 获取DataScope注解
        DataScope dataScope = getDataScopeAnnotation(ms);
        if (dataScope == null) {
            return;
        }

        // 构建数据权限SQL
        String dataScopeSql = buildDataScopeSql(context, dataScope);
        if (dataScopeSql == null || dataScopeSql.isEmpty()) {
            return;
        }

        // 修改原始SQL
        String originalSql = boundSql.getSql();
        String newSql = addDataScopeCondition(originalSql, dataScopeSql);

        // 使用反射修改BoundSql中的sql
        PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(newSql);

        log.debug("数据权限SQL: {}", newSql);
    }

    /**
     * 从当前登录用户构建数据权限上下文
     */
    private DataScopeContext buildContextFromCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        // 从用户权限中获取数据权限范围
        // 这里需要根据实际的UserDetails实现来获取
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 默认返回仅本人数据权限
        DataScopeContext context = new DataScopeContext();
        context.setDataScope(DATA_SCOPE_SELF);

        // 尝试从principal获取用户ID和部门ID
        Object principal = authentication.getPrincipal();
        if (principal instanceof DataScopeUser) {
            DataScopeUser user = (DataScopeUser) principal;
            context.setUserId(user.getUserId());
            context.setDeptId(user.getDeptId());
            context.setDataScope(user.getDataScope());
        }

        return context;
    }

    /**
     * 获取方法上的DataScope注解
     */
    private DataScope getDataScopeAnnotation(MappedStatement ms) {
        try {
            String id = ms.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);

            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.getAnnotation(DataScope.class);
                }
            }
        } catch (Exception e) {
            log.warn("获取DataScope注解失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 构建数据权限SQL条件
     */
    private String buildDataScopeSql(DataScopeContext context, DataScope dataScope) {
        StringBuilder sql = new StringBuilder();
        String ownerField = dataScope.ownerField();
        String deptField = dataScope.deptField();
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();

        // 添加表别名前缀
        if (deptAlias != null && !deptAlias.isEmpty()) {
            deptField = deptAlias + "." + deptField;
        }
        if (userAlias != null && !userAlias.isEmpty()) {
            ownerField = userAlias + "." + ownerField;
        }

        switch (context.getDataScope()) {
            case DATA_SCOPE_DEPT_AND_CHILD:
                // 本部门及下级部门
                sql.append(String.format(
                    " %s IN (SELECT id FROM crm_department WHERE id = %d OR FIND_IN_SET(%d, ancestors))",
                    deptField, context.getDeptId(), context.getDeptId()
                ));
                break;

            case DATA_SCOPE_DEPT:
                // 本部门
                sql.append(String.format(" %s = %d", deptField, context.getDeptId()));
                break;

            case DATA_SCOPE_SELF:
                // 仅本人
                sql.append(String.format(" %s = %d", ownerField, context.getUserId()));
                break;

            default:
                // 全部数据权限，不添加条件
                break;
        }

        return sql.toString();
    }

    /**
     * 在原始SQL中添加数据权限条件
     */
    private String addDataScopeCondition(String originalSql, String dataScopeSql) {
        // 查找WHERE子句的位置
        String upperSql = originalSql.toUpperCase();
        int whereIndex = upperSql.lastIndexOf(" WHERE ");
        int orderIndex = upperSql.lastIndexOf(" ORDER BY ");
        int groupIndex = upperSql.lastIndexOf(" GROUP BY ");
        int limitIndex = upperSql.lastIndexOf(" LIMIT ");

        // 确定插入位置
        int insertIndex;
        if (orderIndex > 0) {
            insertIndex = orderIndex;
        } else if (groupIndex > 0) {
            insertIndex = groupIndex;
        } else if (limitIndex > 0) {
            insertIndex = limitIndex;
        } else {
            insertIndex = originalSql.length();
        }

        StringBuilder newSql = new StringBuilder();
        if (whereIndex > 0) {
            // 已有WHERE子句，添加AND条件
            newSql.append(originalSql, 0, insertIndex);
            newSql.append(" AND ").append(dataScopeSql);
            newSql.append(originalSql.substring(insertIndex));
        } else {
            // 没有WHERE子句，添加WHERE条件
            newSql.append(originalSql, 0, insertIndex);
            newSql.append(" WHERE ").append(dataScopeSql);
            newSql.append(originalSql.substring(insertIndex));
        }

        return newSql.toString();
    }

    /**
     * 数据权限上下文
     */
    public static class DataScopeContext {
        private Long userId;
        private Long deptId;
        private Integer dataScope;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Integer getDataScope() {
            return dataScope;
        }

        public void setDataScope(Integer dataScope) {
            this.dataScope = dataScope;
        }
    }

    /**
     * 数据权限用户接口
     * UserDetails实现类需要实现此接口以支持数据权限
     */
    public interface DataScopeUser {
        Long getUserId();
        Long getDeptId();
        Integer getDataScope();
    }
}
