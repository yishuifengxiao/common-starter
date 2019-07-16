package com.yishuifengxiao.common.properties.social;
/**
 * 微信登陆相关的配置
 * @author yishui
 * @date 2019年7月16日
 * @version 1.0.0
 */
public class WeixinProperties {


	
	private String appId;
	
	private String appSecret;
	

	 
	 private String registerUrl;
	
	/**
	 * 服务提供商id，默认为weixin
	 */
	private String providerId="weixin";

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

	/**
	 * 服务提供商id，默认为weixin
	 */
	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}



	public String getRegisterUrl() {
		return registerUrl;
	}

	public void setRegisterUrl(String registerUrl) {
		this.registerUrl = registerUrl;
	}
	
	

}
