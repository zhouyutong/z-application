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

    public static final ErrorCode PARAM_REQUIRED = new ErrorCode("4001", "参数[%s]不能为空");
    public static final ErrorCode PARAM_INVALID = new ErrorCode("4002", "参数[%s]格式无效");
    public static final ErrorCode SIGN = new ErrorCode("4003", "参数签名验证失败");
    public static final ErrorCode AUTHORITY = new ErrorCode("4004", "您没有权限访问该接口");
    public static final ErrorCode DECRYPT = new ErrorCode("4005", "请求数据解密失败");
    public static final ErrorCode INVALID_CALLER = new ErrorCode("4006", "无效的调用方");
    public static final ErrorCode UPLOAD_TYPE = new ErrorCode("4007", "您上传的文件类型不允许");
    public static final ErrorCode UPLOAD_SIZE = new ErrorCode("4008", "您上传的文件大小不能超过[%s]KB");
    public static final ErrorCode SYSTEM_BUSY = new ErrorCode("4009", "服务器繁忙，请稍后重试");
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
