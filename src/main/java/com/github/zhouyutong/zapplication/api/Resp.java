package com.github.zhouyutong.zapplication.api;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 通用远程调用的响应对象
 */
@ToString
public class Resp implements Serializable {

    public static final String CODE_SUCCESS = "0";
    public static final String MESSAGE_SUCCESS = "success";

    /**
     * 状态码
     */
    @Getter
    private String code;
    /**
     * 状态信息
     */
    @Getter
    private String msg;

    /**
     * 数据对象
     */
    @Getter
    private Map<String, Object> data;

    private Resp(String code, String msg, Map<String, Object> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Resp success(String key, Object value) {
        Map<String, Object> data = Maps.newHashMap();
        data.put(key, value);
        return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, data);
    }

    public static Resp success(String msg) {
        return new Resp(CODE_SUCCESS, msg, null);
    }


    public static Resp success(Map<String, Object> data) {
        return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, data);
    }

    public static Resp success() {
        return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, null);
    }

    public static Resp error(ErrorCode errorCode, String... message) {
        return new Resp(errorCode.getCode(), String.format(errorCode.getMessage(), message), null);
    }

    public static Resp error(String code, String message) {
        return new Resp(code, message, null);
    }

    public static Resp error(ErrorCode errorCode, Map<String, Object> data) {
        return new Resp(errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static Resp error(ErrorCode code) {
        return new Resp(code.getCode(), code.getMessage(), null);
    }

    public boolean respIsSuccess() {
        return CODE_SUCCESS.equals(this.code);
    }

    public Resp append(String key, Object value) {
        if (this.data == null) {
            this.data = Maps.newHashMap();
        }
        this.data.put(key, value);
        return this;
    }
}
