package com.crm.common.enums;

/**
 * 错误码枚举
 * 统一定义系统中的所有错误码
 *
 * 错误码规则:
 * - 10xxx: 系统级错误
 * - 20xxx: 认证授权错误
 * - 30xxx: 参数校验错误
 * - 40xxx: 业务逻辑错误
 * - 50xxx: 外部服务错误
 *
 * @author CRM System
 * @since 1.0.0
 */
public enum ErrorCode {

    // ========== 系统级错误 10xxx ==========
    SUCCESS(0, "操作成功"),
    SYSTEM_ERROR(10000, "系统繁忙，请稍后重试"),
    PARAM_ERROR(10001, "参数错误"),
    DATA_NOT_FOUND(10002, "数据不存在"),
    DATA_ALREADY_EXISTS(10003, "数据已存在"),
    OPERATION_FAILED(10004, "操作失败"),
    FILE_UPLOAD_ERROR(10005, "文件上传失败"),
    FILE_NOT_FOUND(10006, "文件不存在"),
    FILE_TYPE_NOT_ALLOWED(10007, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(10008, "文件大小超出限制"),

    // ========== 认证授权错误 20xxx ==========
    UNAUTHORIZED(20000, "未登录或登录已过期"),
    TOKEN_INVALID(20001, "Token无效"),
    TOKEN_EXPIRED(20002, "Token已过期"),
    ACCESS_DENIED(20003, "没有访问权限"),
    ACCOUNT_DISABLED(20004, "账号已被禁用"),
    ACCOUNT_LOCKED(20005, "账号已被锁定"),
    USERNAME_OR_PASSWORD_ERROR(20006, "用户名或密码错误"),
    PASSWORD_ERROR(20007, "密码错误"),
    OLD_PASSWORD_ERROR(20008, "原密码错误"),
    CAPTCHA_ERROR(20009, "验证码错误"),
    CAPTCHA_EXPIRED(20010, "验证码已过期"),

    // ========== 参数校验错误 30xxx ==========
    PARAM_MISSING(30000, "缺少必要参数"),
    PARAM_TYPE_ERROR(30001, "参数类型错误"),
    PARAM_FORMAT_ERROR(30002, "参数格式错误"),
    PARAM_VALUE_INVALID(30003, "参数值无效"),
    PARAM_LENGTH_EXCEEDED(30004, "参数长度超出限制"),

    // ========== 用户模块错误 40xxx ==========
    USER_NOT_FOUND(40000, "用户不存在"),
    USER_ALREADY_EXISTS(40001, "用户已存在"),
    USERNAME_ALREADY_EXISTS(40002, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(40003, "邮箱已被使用"),
    PHONE_ALREADY_EXISTS(40004, "手机号已被使用"),
    ROLE_NOT_FOUND(40010, "角色不存在"),
    ROLE_ALREADY_EXISTS(40011, "角色已存在"),
    ROLE_IN_USE(40012, "角色正在使用中，无法删除"),
    DEPT_NOT_FOUND(40020, "部门不存在"),
    DEPT_HAS_CHILDREN(40021, "存在下级部门，无法删除"),
    DEPT_HAS_USERS(40022, "部门下存在用户，无法删除"),

    // ========== 业务模块错误 41xxx ==========
    LEAD_NOT_FOUND(41000, "线索不存在"),
    LEAD_ALREADY_CONVERTED(41001, "线索已转化"),
    LEAD_STATUS_ERROR(41002, "线索状态不允许此操作"),
    CUSTOMER_NOT_FOUND(41010, "客户不存在"),
    CUSTOMER_IN_POOL(41011, "客户已在公海池"),
    CUSTOMER_NOT_IN_POOL(41012, "客户不在公海池"),
    CONTACT_NOT_FOUND(41020, "联系人不存在"),
    OPPORTUNITY_NOT_FOUND(41030, "商机不存在"),
    OPPORTUNITY_CLOSED(41031, "商机已关闭"),
    OPPORTUNITY_STAGE_ERROR(41032, "商机阶段不允许此操作"),
    PRODUCT_NOT_FOUND(41040, "产品不存在"),
    PRODUCT_DISABLED(41041, "产品已停用"),
    CONTRACT_NOT_FOUND(41050, "合同不存在"),
    CONTRACT_STATUS_ERROR(41051, "合同状态不允许此操作"),
    PAYMENT_NOT_FOUND(41060, "回款记录不存在"),
    PAYMENT_PLAN_NOT_FOUND(41061, "回款计划不存在"),
    PAYMENT_AMOUNT_EXCEEDED(41062, "回款金额超出计划金额"),

    // ========== 外部服务错误 50xxx ==========
    EXTERNAL_SERVICE_ERROR(50000, "外部服务调用失败"),
    AI_SERVICE_ERROR(50001, "AI服务调用失败"),
    SMS_SERVICE_ERROR(50002, "短信服务调用失败"),
    EMAIL_SERVICE_ERROR(50003, "邮件服务调用失败"),
    WECHAT_SERVICE_ERROR(50004, "企业微信服务调用失败"),
    DINGTALK_SERVICE_ERROR(50005, "钉钉服务调用失败"),
    FEISHU_SERVICE_ERROR(50006, "飞书服务调用失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return SYSTEM_ERROR;
    }
}
