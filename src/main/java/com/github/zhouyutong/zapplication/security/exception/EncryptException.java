package com.github.zhouyutong.zapplication.security.exception;

/**
 * 解密异常
 */
@SuppressWarnings("serial")
public class EncryptException extends RuntimeException {

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
