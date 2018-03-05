package com.zhouyutong.zapplication.sms;

public class SmsException extends RuntimeException {

    public SmsException(String message) {
        super(message);
    }

    public SmsException(String message, Throwable ex) {
        super(message, ex);
    }
}
