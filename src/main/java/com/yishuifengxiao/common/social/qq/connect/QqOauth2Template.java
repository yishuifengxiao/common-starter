package com.yishuifengxiao.common.social.qq.connect;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * <p>传入的参数为 ：clientId，clientSecret，authorizeUrl，accessTokenUrl</p>
 * <p>输出的响应为 ：accessToken ，expiresIn ，refreshToken</p>
 * 
 * <p>根据 【clientId】，【clientSecret】，【authorizeUrl】，【accessTokenUrl】 构造OAuth2Template</p>
 * 
 * <p>生成的对象 供给 AbstractOAuth2ServiceProvider 使用 </p>
 * 
 * <p>根据传入的参数 生成 accessToken，expiresIn，refreshToken </p>
 * 
 * 文档API参见 https://wiki.connect.qq.com/%E5%BC%80%E5%8F%91%E6%94%BB%E7%95%A5_server-side
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class QqOauth2Template extends OAuth2Template {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public QqOauth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		// clientId就是appId
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		setUseParametersForClientAuthentication(true);
	}

	@Override
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		String responseStr = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
		logger.info("【QQOAuth2Template】获取accessToke的响应：responseStr={}" + responseStr);

		String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(responseStr, "&");
		// http://wiki.connect.qq.com/使用authorization_code获取access_token
		// access_token=FE04************************CCE2&expires_in=7776000&refresh_token=88E4************************BE14
		String accessToken = StringUtils.substringAfterLast(items[0], "=");
		Long expiresIn = new Long(StringUtils.substringAfterLast(items[1], "="));
		String refreshToken = StringUtils.substringAfterLast(items[2], "=");

		return new AccessGrant(accessToken, null, refreshToken, expiresIn);
	}

	@Override
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}
}