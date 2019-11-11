package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;

/**
 * 当参数中不存在token时的提示信息 处理器事件
 * 
 * @author yishui
 * @Date 2019年5月30日
 * @version 1.0.0
 */
public class LogoutSuccessEvent extends AbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1240683622233923579L;

	public LogoutSuccessEvent(Authentication authentication, HttpServletRequest request) {
		super(authentication, request);
	}

	@Override
	public Authentication getSource() {

		return (Authentication) super.getSource();
	}

}
