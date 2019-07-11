package com.yishuifengxiao.common.security.event;

import javax.servlet.http.HttpServletRequest;

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

	public LogoutSuccessEvent(Object source, HttpServletRequest request) {
		super(source, request);
	}

}
