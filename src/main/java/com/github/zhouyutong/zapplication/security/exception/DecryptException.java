package com.github.zhouyutong.zapplication.security.exception;

/**
 * 加密异常
 */
@SuppressWarnings("serial")
public class DecryptException extends RuntimeException {

    public DecryptException(String message) {
        super(message);
    }

    public DecryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
