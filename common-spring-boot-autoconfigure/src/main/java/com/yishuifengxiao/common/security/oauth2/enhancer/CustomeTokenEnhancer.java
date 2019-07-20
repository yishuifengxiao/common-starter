package com.yishuifengxiao.common.security.oauth2.enhancer;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.yishuifengxiao.common.tool.random.UID;

/**
 * 自定义token生成器
 * 
 * @author yishui
 * @date 2019年4月1日
 * @version 1.0.0
 */
public class CustomeTokenEnhancer implements TokenEnhancer {

	private final static Logger log = LoggerFactory.getLogger(CustomeTokenEnhancer.class);

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		log.debug("自定义token生成器中得到的初始化token为 {} ,初始化认证信息为 {}", accessToken, authentication);
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
			token.setValue(getNewToken());
			OAuth2RefreshToken refreshToken = token.getRefreshToken();
			if (refreshToken instanceof DefaultOAuth2RefreshToken) {
				token.setRefreshToken(new DefaultOAuth2RefreshToken(getNewToken()));
			}
			Map<String, Object> additionalInformation = new HashMap<String, Object>();
			additionalInformation.put("client_id", authentication.getOAuth2Request().getClientId());
			token.setAdditionalInformation(additionalInformation);
			return token;
		}
		return accessToken;
	}

	/**
	 * 自定义token生成方式
	 * 
	 * @return
	 */
	private String getNewToken() {
		return UID.uuid();
	}

}
