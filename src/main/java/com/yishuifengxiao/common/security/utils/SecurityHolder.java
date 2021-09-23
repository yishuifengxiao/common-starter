package com.yishuifengxiao.common.security.utils;

import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;

/**
 * <p>
 * 自定义异常信息存储类
 * </p>
 * 在各种<code>Handler</code>中例如<code>ExceptionAuthenticationEntryPoint</code>和<code>CustomAuthenticationFailureHandler</code>中携带异常信息
 * 
 * @see CustomAuthenticationFailureHandler
 * @see CustomAccessDeniedHandler
 * @see ExceptionAuthenticationEntryPoint
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecurityHolder {

	private static final ThreadLocal<Exception> CONTEXT_EXCEPTION = new ThreadLocal<>();

	public static final synchronized Exception getException(boolean clear) {
		Exception e = CONTEXT_EXCEPTION.get();
		if (clear) {
			CONTEXT_EXCEPTION.remove();
		}
		return e;
	}

	public static final synchronized void setException(Exception exception) {
		CONTEXT_EXCEPTION.set(exception);
	}

}
