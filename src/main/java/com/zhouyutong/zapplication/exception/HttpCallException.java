package com.zhouyutong.zapplication.exception;

/**
 * http调用异常
 */
@SuppressWarnings("serial")
public class HttpCallException extends RemoteCallException {

    public HttpCallException(String message) {
        super(message);
    }

    public HttpCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
