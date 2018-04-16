package com.zhouyutong.zapplication.exception;

/**
 * cassandra调用异常
 */
@SuppressWarnings("serial")
public class CassandraCallException extends RemoteCallException {

    public CassandraCallException(String message) {
        super(message);
    }

    public CassandraCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
