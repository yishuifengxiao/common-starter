package com.yishuifengxiao.common.security.support;

import org.springframework.context.ApplicationEvent;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * secyrity token 过期事件
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenExpireEvnet extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7359448398147478109L;

	private CustomException cxception;

	private SecurityToken token;

	private String tokenValue;

	public TokenExpireEvnet(Object source, CustomException cxception, SecurityToken token, String tokenValue) {
		super(source);
		this.cxception = cxception;
		this.token = token;
		this.tokenValue = tokenValue;
	}

	public CustomException getCxception() {
		return cxception;
	}

	public void setCxception(CustomException cxception) {
		this.cxception = cxception;
	}

	public SecurityToken getToken() {
		return token;
	}

	public void setToken(SecurityToken token) {
		this.token = token;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

}
