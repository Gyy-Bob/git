package com.gyy.common.result;

/**
 * 全局业务错误码（对应 docs/backend/02-接口规范与Apifox.md §1.1）
 */
public enum ErrorCode {

    OK(0, "success"),

    // ---- 4xx00 客户端参数类 ----
    PARAM_MISSING(40001, "缺少必填参数"),
    PARAM_INVALID(40002, "参数格式错误"),
    PARAM_OUT_OF_RANGE(40003, "参数超出允许范围"),

    // ---- 40100 / 40300 鉴权 ----
    UNAUTHORIZED(40100, "未登录或凭证无效"),
    FORBIDDEN(40300, "无权限访问"),

    // ---- 40400 资源 ----
    NOT_FOUND(40400, "资源不存在"),

    // ---- 业务扩展（见 docs/backend/04-业务逻辑文档.md §4） ----
    LOGIN_FAILED(40101, "用户名或密码错误"),
    ACCOUNT_DISABLED(40302, "账号已被禁用，请联系管理员"),
    USERNAME_TAKEN(40901, "用户名已被注册"),

    // ---- 5xx00 服务端 ----
    INTERNAL_ERROR(50000, "服务内部错误"),
    DOWNSTREAM_TIMEOUT(50001, "下游服务超时"),
    DATA_CONFLICT(50002, "数据冲突");

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
}
