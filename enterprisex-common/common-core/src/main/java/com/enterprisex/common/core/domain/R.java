package com.enterprisex.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回结果类
 *
 * @author EnterpriseX
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL = 500;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis();
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回结果
     */
    public static <T> R<T> ok() {
        return new R<>(SUCCESS, "操作成功");
    }

    /**
     * 成功返回结果
     *
     * @param msg 返回消息
     */
    public static <T> R<T> ok(String msg) {
        return new R<>(SUCCESS, msg);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param msg  返回消息
     * @param data 返回数据
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> R<T> fail() {
        return new R<>(FAIL, "操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param msg 返回消息
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL, msg);
    }

    /**
     * 失败返回结果
     *
     * @param code 状态码
     * @param msg  返回消息
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    /**
     * 失败返回结果
     *
     * @param msg  返回消息
     * @param data 返回数据
     */
    public static <T> R<T> fail(String msg, T data) {
        return new R<>(FAIL, msg, data);
    }

    /**
     * 根据影响行数返回结果
     *
     * @param rows 影响行数
     */
    public static R<Void> toAjax(int rows) {
        return rows > 0 ? ok() : fail();
    }

    /**
     * 根据布尔值返回结果
     *
     * @param result 布尔值
     */
    public static R<Void> toAjax(boolean result) {
        return result ? ok() : fail();
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS == code;
    }

    /**
     * 判断是否失败
     */
    public boolean isFail() {
        return !isSuccess();
    }
}
