package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;

/**
 * 退出成功的事件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionAuthenticationEntryPointEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1244683622233923579L;
	private HttpServletRequest request;
	private Exception authException;

	public ExceptionAuthenticationEntryPointEvent(Object source, HttpServletRequest request, Exception authException) {
		super(source);
		this.request = request;
		this.authException = authException;
	}

	public Exception getAuthException() {
		return this.authException;
	}

	public void setAuthException(Exception authException) {
		this.authException = authException;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
