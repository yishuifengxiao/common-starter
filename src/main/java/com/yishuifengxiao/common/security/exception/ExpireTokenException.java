package com.yishuifengxiao.common.security.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * token 过期异常
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExpireTokenException extends AccessDeniedException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3678260187783988310L;
	private int code;

    public ExpireTokenException(String msg) {
        super(msg);
    }

    public ExpireTokenException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ExpireTokenException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
