package com.yishuifengxiao.common.security.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * 无效token异常
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InvalidTokenException extends AccessDeniedException {

    private int code;

    public InvalidTokenException(String msg) {
        super(msg);
    }

    public InvalidTokenException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public InvalidTokenException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }
}
