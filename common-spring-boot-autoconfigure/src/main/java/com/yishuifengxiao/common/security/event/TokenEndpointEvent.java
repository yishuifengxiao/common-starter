package com.yishuifengxiao.common.security.event;

import org.springframework.context.ApplicationEvent;

import com.yishuifengxiao.common.security.entity.TokenInfo;

/**
 * 访问token端点时的消息
 * 
 * @author yishui
 * @date 2019年10月11日
 * @version 1.0.0
 */
public class TokenEndpointEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8848321429246954640L;

	public TokenEndpointEvent(TokenInfo tokenInfo) {
		super(tokenInfo);
	}

}
