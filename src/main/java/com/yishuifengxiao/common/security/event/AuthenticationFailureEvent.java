package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;

/**
 * 认证失败的事件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class AuthenticationFailureEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2240683622233923579L;

	private HttpServletRequest request;
	private Exception exception;

	public AuthenticationFailureEvent(Object source, HttpServletRequest request, Exception exception) {
		super(source);
		this.request = request;
		this.exception = exception;
	}

	public Exception getException() {
		return this.exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
