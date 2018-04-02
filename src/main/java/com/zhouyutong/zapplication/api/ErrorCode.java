package com.zhouyutong.zapplication.api;

import lombok.Getter;

import java.io.Serializable;

/**
 * 错误码
 * 400开头的是公共错误码
 * 其他如4100、4200等开头各模块自行定义
 * 只需要继承ErrorCode就可以
 *
 * Created by cheshun on 16/9/2.
 *
 */
public class ErrorCode implements Serializable {

    public static final ErrorCode PARAM_REQUIRED = new ErrorCode("4001", "必填参数【%s】为空");
    public static final ErrorCode PARAM_INVALID = new ErrorCode("4006", "参数格式无效【%s】");
    public static final ErrorCode SIGN = new ErrorCode("4002", "签名验证失败");
    public static final ErrorCode AUTHORITY = new ErrorCode("4003", "无权限做此操作");
    public static final ErrorCode DECRYPT = new ErrorCode("4004", "解密失败");
    public static final ErrorCode INVALID_CALLER = new ErrorCode("4005", "非法调用方");
    public static final ErrorCode UPLOAD_TYPE = new ErrorCode("4007", "文件类型只允许上传【%s】");
    public static final ErrorCode UPLOAD_SIZE = new ErrorCode("4008", "文件大小不可超过【%s】M");
    /**
     * 5000通用服务器异常
     * 5001访问redis异常
     * 5002访问db异常
     */
    public static final ErrorCode SERVER = new ErrorCode("5000", "服务器异常【%s】");
    public static final ErrorCode SERVER_REDIS = new ErrorCode("5001", "服务器异常【%s】");
    public static final ErrorCode SERVER_DAO = new ErrorCode("5002", "服务器异常【%s】");

    @Getter
    private String code;
    @Getter
    private String message;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
