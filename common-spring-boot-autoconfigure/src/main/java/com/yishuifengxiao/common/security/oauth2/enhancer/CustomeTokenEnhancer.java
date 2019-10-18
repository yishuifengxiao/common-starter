package com.yishuifengxiao.common.security.oauth2.enhancer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
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

	/**
	 * 时间格式化的形式
	 */
	private final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 时区
	 */
	private final static String ZONE_ID = "Asia/Shanghai";

	private ObjectMapper om = new ObjectMapper();

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		log.debug("自定义token生成器中得到的初始化token为 {} ,初始化认证信息为 {}", accessToken, authentication);
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			DefaultOAuth2AccessToken token = ((DefaultOAuth2AccessToken) accessToken);
			token.setValue(getNewToken(accessToken, authentication));
			OAuth2RefreshToken refreshToken = token.getRefreshToken();
			if (refreshToken instanceof DefaultOAuth2RefreshToken) {
				token.setRefreshToken(new DefaultOAuth2RefreshToken(UID.uuid()));
			}
			Map<String, Object> additionalInformation = new HashMap<String, Object>(1);
			additionalInformation.put("developer", "yishuifengxiao");
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
	private String getNewToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		// 获取到认证信息
		Authentication auth = authentication.getUserAuthentication();
		// 用户名
		String username = auth != null ? auth.getName() : "";

		// 获取到所有的角色
		Collection<GrantedAuthority> list = authentication.getAuthorities();
		// 获取到clientID
		String clientId = authentication.getOAuth2Request().getClientId();
		// 授权模式
		String grantType = authentication.getOAuth2Request().getGrantType();
		// 生成token的时间
		String time = LocalDateTime.now(ZoneId.of(ZONE_ID)).format(DateTimeFormatter.ofPattern(PATTERN));

		CustomToken customToken = new CustomToken(username, clientId,
				list.stream().map(t -> t.getAuthority()).collect(Collectors.toList()), grantType, time,
				accessToken.getExpiresIn());
		String token = customToken.toString();
		try {
			token = om.writeValueAsString(customToken);
		} catch (JsonProcessingException e) {

		}

		return DES.encrypt(token);
	}

}
