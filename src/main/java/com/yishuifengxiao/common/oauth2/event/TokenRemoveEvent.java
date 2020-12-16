package com.yishuifengxiao.common.oauth2.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * 删除oauth2 token时的事件信息
 * 
 * @author yishui
 * @date 2019年10月31日
 * @version 1.0.0
 */
public class TokenRemoveEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710651173868223306L;

	private OAuth2AccessToken oAuth2AccessToken;

	public OAuth2AccessToken getoAuth2AccessToken() {
		return oAuth2AccessToken;
	}

	public void setoAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

	public TokenRemoveEvent(Object source, OAuth2AccessToken oAuth2AccessToken) {
		super(source);
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

}
