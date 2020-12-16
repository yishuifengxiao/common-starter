package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 * 推出登陆 处理器事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class LogoutSuccessEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240683522233923579L;
	private HttpServletRequest request;

	private Authentication authentication;

	public LogoutSuccessEvent(Object source, HttpServletRequest request, Authentication authentication) {
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
