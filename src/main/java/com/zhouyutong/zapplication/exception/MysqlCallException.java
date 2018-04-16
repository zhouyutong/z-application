package com.zhouyutong.zapplication.exception;

/**
 * mysql调用异常
 */
@SuppressWarnings("serial")
public class MysqlCallException extends RemoteCallException {

    public MysqlCallException(String message) {
        super(message);
    }

    public MysqlCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
