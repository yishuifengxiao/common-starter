package com.yishuifengxiao.common.security.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * 异常账号异常
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class AbnormalAccountException extends AccessDeniedException {

    private int code;

    public AbnormalAccountException(String msg) {
        super(msg);
    }

    public AbnormalAccountException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public AbnormalAccountException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }
}
