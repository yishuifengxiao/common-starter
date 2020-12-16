package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.AuthenticationException;

/**
 * 认证失败的事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class AuthenticationFailureEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2240683622233923579L;

	private HttpServletRequest request;
	private AuthenticationException exception;

	public AuthenticationFailureEvent(Object source, HttpServletRequest request, AuthenticationException exception) {
		super(source);
		this.request = request;
		this.exception = exception;
	}

	public AuthenticationException getException() {
		return this.exception;
	}

	public void setException(AuthenticationException exception) {
		this.exception = exception;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
