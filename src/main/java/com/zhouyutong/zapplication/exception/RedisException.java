package com.zhouyutong.zapplication.exception;

/**
 * redis异常
 */
@SuppressWarnings("serial")
public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}
