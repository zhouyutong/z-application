package com.zhouyutong.zapplication.exception;

/**
 * es调用异常
 */
@SuppressWarnings("serial")
public class ElasticsearchCallException extends RemoteCallException {

    public ElasticsearchCallException(String message) {
        super(message);
    }

    public ElasticsearchCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
