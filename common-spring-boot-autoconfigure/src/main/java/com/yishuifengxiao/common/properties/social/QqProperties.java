package com.yishuifengxiao.common.properties.social;
/**
 * QQ登陆相关的配置
 * @author yishui
 * @date 2019年7月14日
 * @version 1.0.0
 */
public class QqProperties {

	
	private String appId;
	
	private String appSecuret;
	
	/**
	 * 服务提供商id，默认为qq
	 */
	private String providerId="qq";

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecuret() {
		return appSecuret;
	}

	public void setAppSecuret(String appSecuret) {
		this.appSecuret = appSecuret;
	}

	/**
	 * 服务提供商id，默认为qq
	 */
	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	
}
