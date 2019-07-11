package com.yishuifengxiao.common.properties.security;

import com.yishuifengxiao.common.constant.SecurityConstant;

/**
 * spring security session相关的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class SessionProperties {
	/**
	 * 同一个用户在系统中的最大session数，默认1
	 */
	private int maximumSessions = 1;
	/**
	 * 达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
	 */
	private boolean maxSessionsPreventsLogin = false;
	/**
	 * session失效时跳转的地址
	 */
	private String sessionInvalidUrl = SecurityConstant.DEFAULT_SESSION_INVALID_URL;

	/**
	 * 同一个用户在系统中的最大session数，默认1
	 */
	public int getMaximumSessions() {
		return maximumSessions;
	}

	public void setMaximumSessions(int maximumSessions) {
		this.maximumSessions = maximumSessions;
	}

	/**
	 * 达到最大session时是否阻止新的登录请求，默认为false，不阻止，新的登录会将老的登录失效掉
	 */
	public boolean isMaxSessionsPreventsLogin() {
		return maxSessionsPreventsLogin;
	}

	public void setMaxSessionsPreventsLogin(boolean maxSessionsPreventsLogin) {
		this.maxSessionsPreventsLogin = maxSessionsPreventsLogin;
	}

	/**
	 * session失效时跳转的地址
	 */
	public String getSessionInvalidUrl() {
		return sessionInvalidUrl;
	}

	public void setSessionInvalidUrl(String sessionInvalidUrl) {
		this.sessionInvalidUrl = sessionInvalidUrl;
	}
}
