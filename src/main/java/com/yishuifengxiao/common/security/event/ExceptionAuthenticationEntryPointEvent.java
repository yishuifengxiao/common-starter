package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.AuthenticationException;

/**
 * 退出成功的事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class ExceptionAuthenticationEntryPointEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1244683622233923579L;
	private HttpServletRequest request;
	private AuthenticationException authException;

	public ExceptionAuthenticationEntryPointEvent(Object source, HttpServletRequest request,
			AuthenticationException authException) {
		super(source);
		this.request = request;
		this.authException = authException;
	}

	public AuthenticationException getAuthException() {
		return this.authException;
	}

	public void setAuthException(AuthenticationException authException) {
		this.authException = authException;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
