package com.yishuifengxiao.common.oauth2.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 生成oauth2 token时的事件信息
 * 
 * @author yishui
 * @date 2019年10月31日
 * @version 1.0.0
 */
public class TokenGenerateEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710651173868223306L;

	/**
	 * 是否为新生成的token还是已经生成的但是还在有效期内的token
	 */
	private boolean existing;
	/**
	 * 认证信息
	 */
	private OAuth2Authentication authentication;
	/**
	 * token信息
	 */
	private OAuth2AccessToken oAuth2AccessToken;

	public TokenGenerateEvent(Object source, boolean existing, OAuth2Authentication authentication,
			OAuth2AccessToken oAuth2AccessToken) {
		super(source);
		this.existing = existing;
		this.authentication = authentication;
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

	public OAuth2AccessToken getoAuth2AccessToken() {
		return oAuth2AccessToken;
	}

	public void setoAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

	public boolean isExisting() {
		return existing;
	}

	public void setExisting(boolean existing) {
		this.existing = existing;
	}

	public OAuth2Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(OAuth2Authentication authentication) {
		this.authentication = authentication;
	}

}
