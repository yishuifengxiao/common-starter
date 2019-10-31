package com.yishuifengxiao.common.security.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
/**
 * 删除oauth2 token时的事件信息
 * @author yishui
 * @date 2019年10月31日
 * @version 1.0.0
 */
public class TokenRemoveEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6710651173868223306L;

	public TokenRemoveEvent(OAuth2AccessToken oAuth2AccessToken) {
		super(oAuth2AccessToken);
	}

	@Override
	public OAuth2AccessToken getSource() {
		return (OAuth2AccessToken) super.getSource();
	}
	
	

}
