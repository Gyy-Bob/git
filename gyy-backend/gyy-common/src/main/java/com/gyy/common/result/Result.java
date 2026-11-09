package com.gyy.common.result;

import org.slf4j.MDC;

/**
 * 统一响应体：Result&lt;T&gt;{code, message, data, traceId}
 * traceId 与链路追踪（Micrometer Tracing / Zipkin）对齐，取 MDC 中的 traceId。
 */
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private String traceId;

    public Result() {
    }

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.traceId = currentTraceId();
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), data);
    }

    public static Result<Void> ok() {
        return ok(null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    private static String currentTraceId() {
        String traceId = MDC.get("traceId");
        return traceId == null ? "N/A" : traceId;
    }

    public boolean isSuccess() {
        return this.code == ErrorCode.OK.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
