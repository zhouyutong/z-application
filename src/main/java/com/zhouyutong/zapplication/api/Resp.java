package com.zhouyutong.zapplication.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通用远程调用的响应对象
 */
@ApiModel
@ToString
public class Resp<T> implements Serializable {
    public static final String CODE_SUCCESS = "0";
    public static final String MESSAGE_SUCCESS = "success";

    @ApiModelProperty(value = "状态码,0成功非0失败")
    @Getter
    private String code;

    @ApiModelProperty(value = "状态码描述,0success非0失败原因")
    @Getter
    private String msg;

    @ApiModelProperty(value = "返回的数据对象")
    @Getter
    private T data;

    private Resp(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Resp success() {
        return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, null);
    }

    public static <T> Resp success(T data) {
        return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, data);
    }

    public static Resp error(ErrorCode code) {
        return new Resp(code.getCode(), code.getMessage(), null);
    }

    public static Resp error(ErrorCode errorCode, String message) {
        return new Resp(errorCode.getCode(), String.format(errorCode.getMessage(), message), null);
    }

    public static Resp error(String code, String message) {
        return new Resp(code, message, null);
    }

    public boolean hasSuccess() {
        return CODE_SUCCESS.equals(this.code);
    }
}
