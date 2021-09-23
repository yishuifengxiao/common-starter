package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 * 认证成功的事件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class AuthenticationSuccessEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1340683622233923579L;
	private HttpServletRequest request;

	private Authentication authentication;

	public AuthenticationSuccessEvent(Object source, HttpServletRequest request, Authentication authentication) {
		super(source);
		this.request = request;
		this.authentication = authentication;
	}

	public Authentication getAuthentication() {
		return this.authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
