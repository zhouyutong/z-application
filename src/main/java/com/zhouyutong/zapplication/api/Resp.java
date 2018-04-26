package com.zhouyutong.zapplication.api;

import com.zhouyutong.zapplication.exception.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * 通用远程调用的响应对象
 */
@ApiModel
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

    public static Resp error(Throwable e) {
        //远程访问异常
        if (e instanceof RemoteCallException) {
            if (e instanceof MysqlCallException) {
                return new Resp(ErrorCode.SERVER_MYSQL.getCode(), String.format(ErrorCode.SERVER_MYSQL.getMessage(), e.getMessage()), null);
            } else if (e instanceof RedisCallException) {
                return new Resp(ErrorCode.SERVER_REDIS.getCode(), String.format(ErrorCode.SERVER_REDIS.getMessage(), e.getMessage()), null);
            } else if (e instanceof ElasticsearchCallException) {
                return new Resp(ErrorCode.SERVER_ES.getCode(), String.format(ErrorCode.SERVER_ES.getMessage(), e.getMessage()), null);
            } else if (e instanceof HttpCallException) {
                return new Resp(ErrorCode.SERVER_HTTP.getCode(), String.format(ErrorCode.SERVER_HTTP.getMessage(), e.getMessage()), null);
            } else if (e instanceof CassandraCallException) {
                return new Resp(ErrorCode.SERVER_CASSANDRA.getCode(), String.format(ErrorCode.SERVER_CASSANDRA.getMessage(), e.getMessage()), null);
            } else if (e instanceof OracleCallException) {
                return new Resp(ErrorCode.SERVER_ORACLE.getCode(), String.format(ErrorCode.SERVER_ORACLE.getMessage(), e.getMessage()), null);
            }
        }
        //服务层指定异常
        if (e instanceof ServiceException) {
            ServiceException se = (ServiceException) e;
            return new Resp(se.getCode(), se.getMessage(), null);
        }
        //未知异常
        return new Resp(ErrorCode.SERVER.getCode(), String.format(ErrorCode.SERVER.getMessage(), e.getMessage()), null);
    }

    public static Resp error(String code, String message) {
        return new Resp(code, message, null);
    }

    public boolean hasSuccess() {
        return CODE_SUCCESS.equals(this.code);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Resp{");
        sb.append("code='").append(code).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    public String toSimpleString() {
        final StringBuilder sb = new StringBuilder("Resp{");
        sb.append("code='").append(code).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
