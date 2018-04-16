package com.zhouyutong.zapplication.exception;

/**
 * oracle调用异常
 */
@SuppressWarnings("serial")
public class OracleCallException extends RemoteCallException {

    public OracleCallException(String message) {
        super(message);
    }

    public OracleCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
