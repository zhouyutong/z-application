package com.zhouyutong.zapplication.api;

import com.zhouyutong.zapplication.exception.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 通用远程调用的响应对象
 */
@ApiModel
public class Resp<T> implements Serializable {
	public static final String CODE_SUCCESS = "0";
	public static final String MESSAGE_SUCCESS = "success";

	@ApiModelProperty(value = "0成功非0失败")
	@Getter
	@Setter
	private String code;

	@ApiModelProperty(value = "0success非0失败原因")
	@Getter
	@Setter
	private String message;

	@ApiModelProperty(value = "数据")
	@Getter
	@Setter
	private T data;

	private Resp(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static Resp success() {
		return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, null);
	}

	public static <T> Resp success(T data) {
		return new Resp(CODE_SUCCESS, MESSAGE_SUCCESS, data);
	}

	public static <T> Resp success(T data, String message) {
		return new Resp(CODE_SUCCESS, message, data);
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
			String code = StringUtils.isBlank(se.getCode()) ? ErrorCode.OPER_FAIL.getCode() : se.getCode();
			String msg = StringUtils.isBlank(se.getMessage()) ? ErrorCode.OPER_FAIL.getMessage() : se.getMessage();
			return new Resp(code, msg, null);
		}
		//未知异常
		return new Resp(ErrorCode.SERVER.getCode(), String.format(ErrorCode.SERVER.getMessage(), e.getMessage()), null);
	}

	public static Resp error(ErrorCode errorCode, String appendMessage) {
		String message = String.format(errorCode.getMessage(), appendMessage == null ? "" : appendMessage);
		return new Resp(errorCode.getCode(), message, null);
	}

	public static Resp error(ErrorCode errorCode) {
		return new Resp(errorCode.getCode(), errorCode.getMessage(), null);
	}

	public static Resp error(String code, String message) {
		return new Resp(code, message, null);
	}

	public static Resp error(String message) {
		return new Resp(ErrorCode.OPER_FAIL.getCode(), message, null);
	}

	public boolean hasSuccess() {
		return CODE_SUCCESS.equals(this.code);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("code=").append(code);
		sb.append(",message=").append(message);
		sb.append(",data=").append(data);
		sb.append("}");
		return sb.toString();
	}

	public String toSimpleString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("code=").append(code);
		sb.append(",message=").append(message);
		sb.append("}");
		return sb.toString();
	}
}
