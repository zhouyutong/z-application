package com.zhouyutong.zapplication.api;

import lombok.Getter;

import java.io.Serializable;

/**
 * 错误码
 * 400开头的是公共错误码
 * 其他如4100、4200等开头各模块自行定义
 * 500开头系统保留不要覆盖
 * 只需要继承ErrorCode就可以
 * <p>
 * Created by zhoutao on 16/9/2.
 */
public class ErrorCode implements Serializable {

    public static final ErrorCode PARAM_REQUIRED = new ErrorCode("4001", "Parameter [%s] can not be empty.");
    public static final ErrorCode PARAM_INVALID = new ErrorCode("4002", "Parameter [%s] format is invalid..");
    public static final ErrorCode SIGN = new ErrorCode("4003", "Signature verification failure.");
    public static final ErrorCode AUTHORITY = new ErrorCode("4004", "Do this without permission.");
    public static final ErrorCode DECRYPT = new ErrorCode("4005", "Decryption failure.");
    public static final ErrorCode INVALID_CALLER = new ErrorCode("4006", "Illegal invocation party.");
    public static final ErrorCode UPLOAD_TYPE = new ErrorCode("4007", "File type allowed only [%s] to upload.");
    public static final ErrorCode UPLOAD_SIZE = new ErrorCode("4008", "File size can't exceed [%s]K.");
    /**
     * 5000通用服务器异常,5001访问redis异常
     */
    public static final ErrorCode SERVER = new ErrorCode("5000", "Server error,[%s].");
    public static final ErrorCode SERVER_REDIS = new ErrorCode("5001", "Server error,[%s].");
    public static final ErrorCode SERVER_MYSQL = new ErrorCode("5002", "Server error,[%s].");
    public static final ErrorCode SERVER_ES = new ErrorCode("5003", "Server error,[%s].");
    public static final ErrorCode SERVER_HTTP = new ErrorCode("5004", "Server error,[%s].");
    public static final ErrorCode SERVER_CASSANDRA = new ErrorCode("5005", "Server error,[%s].");
    public static final ErrorCode SERVER_ORACLE = new ErrorCode("5006", "Server error,[%s].");

    @Getter
    private String code;
    @Getter
    private String message;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
