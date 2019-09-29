package com.yishuifengxiao.common.properties.oauth2;

import com.yishuifengxiao.common.constant.Oauth2Constant;

/**
 * 系统内置的终端信息
 * 
 * @author yishui
 * @date 2019年9月29日
 * @version 1.0.0
 */
public class ClientProperties {

	/**
	 * 用于唯一标识每一个客户端(client); 在注册时必须填写(也可由服务端自动生成).<br/>
	 * 对于不同的grant_type,该字段都是必须的. 在实际应用中的另一个名称叫appKey,与client_id是同一个概念.
	 */
	private String clientId;

	/**
	 * 用于指定客户端(client)的访问密匙; 在注册时必须填写(也可由服务端自动生成).<br/>
	 * 对于不同的grant_type,该字段都是必须的. 在实际应用中的另一个名称叫appSecret,与client_secret是同一个概念.
	 */
	private String clientSecret;

	/**
	 * 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials,<br/>
	 * 若支持多个grant_type用逗号(,)分隔,如: "authorization_code,password".
	 */
	private String grantType = Oauth2Constant.DEFAULT_GRANT_TYPE;

	/**
	 * 客户端的重定向URI,可为空, 当grant_type为authorization_code或implicit时,
	 * 在Oauth的流程中会使用并检查与注册时填写的redirect_uri是否一致
	 */
	private String registeredRedirectUris = Oauth2Constant.DEFAULT_URL;

	/**
	 * 指定客户端所拥有的Spring Security的权限值,可选, 若有多个权限值,用逗号(,)分隔, 如: "ROLE_UNITY,ROLE_USER".
	 */
	private String authorities = Oauth2Constant.DEFAULT_AUTHORTY;

	/**
	 * 指定客户端申请的权限范围,可选值包括read,write,trust;若有多个权限范围用逗号(,)分隔,如: "read,write".
	 */
	private String scopes = Oauth2Constant.DEFAULT_SCOPE;
	/**
	 * 设置用户是否自动Approval操作, 默认值为 'false', 可选值包括 'true','false', 'read','write'.
	 */
	private String autoApproveScopes = Oauth2Constant.DEFAULT_APPROVE_SCOPE;
	/**
	 * 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
	 */
	private Integer accessTokenValiditySeconds = Oauth2Constant.TOKEN_VALID_TIME;
	/**
	 * 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
	 */
	private Integer refreshTokenValiditySeconds = Oauth2Constant.TOKEN_REDRESH_TIME;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getRegisteredRedirectUris() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUris(String registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public String getScopes() {
		return scopes;
	}

	public void setScopes(String scopes) {
		this.scopes = scopes;
	}

	public String getAutoApproveScopes() {
		return autoApproveScopes;
	}

	public void setAutoApproveScopes(String autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

}
