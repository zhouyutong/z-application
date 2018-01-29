package com.github.zhouyutong.zapplication.utils.exception;

/**
 * 使用HttpClientUtils时发生的异常
 */
@SuppressWarnings("serial")
public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
