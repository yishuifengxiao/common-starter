package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationEvent;
/**
 * 抽象时间
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public abstract class AbstractEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6871939941540677144L;
	
	protected HttpServletRequest request;

	public AbstractEvent(Object source) {
		super(source);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public AbstractEvent(Object source, HttpServletRequest request) {
		super(source);
		this.request = request;
	}





}
