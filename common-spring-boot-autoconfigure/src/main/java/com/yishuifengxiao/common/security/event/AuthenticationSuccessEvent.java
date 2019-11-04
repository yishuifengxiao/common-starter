package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * 认证成功的事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class AuthenticationSuccessEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240683622233923579L;

	public AuthenticationSuccessEvent(Authentication authentication, HttpServletRequest request) {
		super(authentication, request);
	}

	@Override
	public Authentication getSource() {
		return (Authentication) super.getSource();
	}

}
