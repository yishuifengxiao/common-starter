package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.access.AccessDeniedException;

/**
 * 权限拒绝的事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class AccessDeniedEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240583622233923579L;
	private HttpServletRequest request;

	private AccessDeniedException accessDeniedException;

	public AccessDeniedEvent(Object source, HttpServletRequest request, AccessDeniedException accessDeniedException) {
		super(source);
		this.request = request;
		this.accessDeniedException = accessDeniedException;
	}

	public AccessDeniedException getAccessDeniedException() {
		return this.accessDeniedException;
	}

	public void setAccessDeniedException(AccessDeniedException accessDeniedException) {
		this.accessDeniedException = accessDeniedException;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

}
