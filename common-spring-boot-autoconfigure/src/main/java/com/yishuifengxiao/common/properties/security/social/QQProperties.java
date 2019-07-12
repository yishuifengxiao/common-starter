package com.yishuifengxiao.common.properties.security.social;

/**
 * QQ登陆时的参数
 * 
 * @author yishui
 * @date 2019年7月12日
 * @version 1.0.0
 */
public class QQProperties {
	/**
	 * 服务提供商标识
	 */
	private String providerId = "qq";

	/**
	 * Application id.
	 */
	private String appId;

	/**
	 * Application secret.
	 */
	private String appSecret;

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

}