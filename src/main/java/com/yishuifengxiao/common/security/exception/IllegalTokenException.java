package com.yishuifengxiao.common.security.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * 非法token异常
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class IllegalTokenException extends AccessDeniedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 27959461095842618L;
	private int code;

    public IllegalTokenException(String msg) {
        super(msg);
    }

    public IllegalTokenException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public IllegalTokenException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
