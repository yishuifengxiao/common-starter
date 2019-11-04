package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;

/**
 * 权限拒绝的事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class AccessDeniedEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240683622233923579L;

	public AccessDeniedEvent(AccessDeniedException accessDeniedException, HttpServletRequest request) {
		super(accessDeniedException, request);
	}

	@Override
	public AccessDeniedException getSource() {
		return (AccessDeniedException) super.getSource();
	}

}
