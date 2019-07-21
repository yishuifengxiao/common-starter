package com.yishuifengxiao.common.security.oauth2.enhancer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.security.entity.CustomToken;
import com.yishuifengxiao.common.tool.encoder.DES;
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

	private ObjectMapper om = new ObjectMapper();

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		log.debug("自定义token生成器中得到的初始化token为 {} ,初始化认证信息为 {}", accessToken, authentication);
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
			token.setValue(getNewToken(authentication));
			OAuth2RefreshToken refreshToken = token.getRefreshToken();
			if (refreshToken instanceof DefaultOAuth2RefreshToken) {
				token.setRefreshToken(new DefaultOAuth2RefreshToken(UID.uuid()));
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
	private String getNewToken(OAuth2Authentication authentication) {
		// 用户名
		String username = authentication.getUserAuthentication().getName();

		// 获取到所有的角色
		Collection<GrantedAuthority> list = authentication.getAuthorities();
		// 获取到clientID
		String clientId = authentication.getOAuth2Request().getClientId();
		// 授权模式
		String grantType = authentication.getOAuth2Request().getGrantType();

		CustomToken customToken = new CustomToken(username, clientId,
				list.stream().map(t -> t.getAuthority()).collect(Collectors.toList()), grantType);
		String token = customToken.toString();
		try {
			token = om.writeValueAsString(customToken);
		} catch (JsonProcessingException e) {

		}

		return DES.encrypt(token);
	}

}
