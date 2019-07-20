package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * oauth2相关的配置
 * 
 * @author yishui
 * @date 2019年71月23日
 * @version 0.0.1
 */
@ConfigurationProperties(prefix = "yishuifengxiao.security.oauth2")
public class Oauth2Properties {

	/**
	 * Spring Security access rule for the check token endpoint (e.g. a SpEL expression
	 * like "isAuthenticated()") . Default is empty, which is interpreted as "denyAll()"
	 * (no access).
	 */
	private String checkTokenAccess;

	/**
	 * Spring Security access rule for the token key endpoint (e.g. a SpEL expression like
	 * "isAuthenticated()"). Default is empty, which is interpreted as "denyAll()" (no
	 * access).
	 */
	private String tokenKeyAccess;

	/**
	 * Realm name for client authentication. If an unauthenticated request comes in to the
	 * token endpoint, it will respond with a challenge including this name.
	 */
	private String realm="yishuifengxiao";

	public String getCheckTokenAccess() {
		return this.checkTokenAccess;
	}

	public void setCheckTokenAccess(String checkTokenAccess) {
		this.checkTokenAccess = checkTokenAccess;
	}

	public String getTokenKeyAccess() {
		return this.tokenKeyAccess;
	}

	public void setTokenKeyAccess(String tokenKeyAccess) {
		this.tokenKeyAccess = tokenKeyAccess;
	}

	public String getRealm() {
		return this.realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

}