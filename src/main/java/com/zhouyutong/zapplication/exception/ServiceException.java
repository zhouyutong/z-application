package com.zhouyutong.zapplication.exception;

import com.zhouyutong.zapplication.api.ErrorCode;
import com.zhouyutong.zapplication.constant.SymbolConstant;

/**
 * 服务层统一异常
 * provider统一使用此异常，通过不同的code和message区分不同的错误
 * code和message @see ErrorCode
 */
public class ServiceException extends RuntimeException {
    private String code;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public ServiceException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }

    /**
     * 追加格式化的消息%s
     * ErrorCode PARAM_REQUIRED = new ErrorCode("40001", "必填参数【%s】为空");
     *
     * @param errorCode
     * @param appendMessage
     */
    public ServiceException(ErrorCode errorCode, String appendMessage) {
        super(String.format(errorCode.getMessage(), appendMessage == null ? SymbolConstant.EMPTY : appendMessage));
        this.code = errorCode.getCode();
    }

    public ServiceException(ErrorCode errorCode, String appendMessage, Throwable cause) {
        super(String.format(errorCode.getMessage(), appendMessage == null ? SymbolConstant.EMPTY : appendMessage), cause);
        this.code = errorCode.getCode();
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{");
        sb.append("code=").append(getCode());
        sb.append(",");
        sb.append("message=").append(getLocalizedMessage());
        sb.append('}');
        return sb.toString();
    }
}
