package com.zhouyutong.zapplication.serialization.json;

/**
 * json序列号反序列化异常
 */
@SuppressWarnings("serial")
public class JsonException extends RuntimeException {

    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
