package com.crm.common.utils;

import java.util.regex.Pattern;

/**
 * SQL注入防护工具类
 * 提供SQL注入检测和参数清理功能
 *
 * @author CRM System
 * @since 1.0.0
 */
public class SqlInjectionUtils {

    private SqlInjectionUtils() {
    }

    /**
     * SQL注入关键字模式
     */
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(\\b(select|insert|update|delete|drop|truncate|alter|create|grant|revoke|union|exec|execute|xp_|sp_|0x|declare|cast|convert|char|nchar|varchar|nvarchar|cursor|fetch|kill|sysobjects|syscolumns|information_schema)\\b)" +
            "|(--)|(;)|(/\\*)|(\\')|(\\\")|(\\|\\|)|(&&)",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 排序字段允许的字符模式（只允许字母、数字、下划线、点）
     */
    private static final Pattern SAFE_ORDER_PATTERN = Pattern.compile("^[a-zA-Z0-9_.]+$");

    /**
     * 检测字符串是否包含SQL注入风险
     *
     * @param value 待检测的字符串
     * @return true-包含SQL注入风险，false-安全
     */
    public static boolean hasSqlInjection(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return SQL_INJECTION_PATTERN.matcher(value).find();
    }

    /**
     * 清理可能的SQL注入字符
     *
     * @param value 待清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanSqlInjection(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // 移除危险字符
        return value
                .replace("'", "")
                .replace("\"", "")
                .replace(";", "")
                .replace("--", "")
                .replace("/*", "")
                .replace("*/", "")
                .replace("\\", "");
    }

    /**
     * 验证排序字段是否安全
     * 用于动态排序场景，防止SQL注入
     *
     * @param orderBy 排序字段
     * @return true-安全，false-不安全
     */
    public static boolean isSafeOrderBy(String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            return true;
        }
        return SAFE_ORDER_PATTERN.matcher(orderBy).matches();
    }

    /**
     * 验证并清理排序字段
     *
     * @param orderBy 排序字段
     * @param defaultOrderBy 默认排序字段
     * @return 安全的排序字段
     */
    public static String getSafeOrderBy(String orderBy, String defaultOrderBy) {
        if (orderBy == null || orderBy.isEmpty() || !isSafeOrderBy(orderBy)) {
            return defaultOrderBy;
        }
        return orderBy;
    }

    /**
     * 转义LIKE查询中的特殊字符
     *
     * @param value 待转义的字符串
     * @return 转义后的字符串
     */
    public static String escapeLike(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * 验证表名是否安全
     *
     * @param tableName 表名
     * @return true-安全，false-不安全
     */
    public static boolean isSafeTableName(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return false;
        }
        // 表名只允许字母、数字、下划线，且必须以字母开头
        return tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
    }

    /**
     * 验证列名是否安全
     *
     * @param columnName 列名
     * @return true-安全，false-不安全
     */
    public static boolean isSafeColumnName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            return false;
        }
        // 列名只允许字母、数字、下划线，且必须以字母开头
        return columnName.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
    }
}
