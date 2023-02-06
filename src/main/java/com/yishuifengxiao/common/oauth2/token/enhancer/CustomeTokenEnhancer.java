package com.yishuifengxiao.common.oauth2.token.enhancer;

import org.apache.commons.collections4.MapUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.yishuifengxiao.common.oauth2.token.AccessToken;
import com.yishuifengxiao.common.tool.encoder.DES;

/**
 * <p>
 * 自定义token生成器
 * </p>
 * 由<code> Oauth2Server </code>收集，然后经<code>public void configure(ResourceServerSecurityConfigurer resources) </code>注入到oauth2中
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public class CustomeTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		if (accessToken instanceof DefaultOAuth2AccessToken) {
			return this.getNewToken(accessToken, authentication);
		}
		return accessToken;
	}

	/**
	 * 自定义token生成方式
	 * 
	 * @return 自定义token
	 */
	private AccessToken getNewToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		AccessToken oauth2Token = new AccessToken(accessToken);

		String username = null;

		String clientId = null;

		String grantType = null;

		// 获取到认证信息
		Authentication auth = authentication.getUserAuthentication();

		OAuth2Request oAuth2Request = authentication.getOAuth2Request();

		if (null != oAuth2Request) {
			clientId = oAuth2Request.getClientId();
			grantType = MapUtils.getString(oAuth2Request.getRequestParameters(), "grant_type");
		}

		if (null != auth) {
			// 用户名
			username = auth.getName();
		}

		// 修改token
		oauth2Token.setValue(DES.encrypt(new StringBuffer(null == username ? "" : username).append(":")
				.append(null == clientId ? "" : clientId).append(":").append(null == grantType ? "" : grantType)
				.append(":").append(System.currentTimeMillis()).toString()));
		// 添加一个附加信息
		oauth2Token.addAdditionalInformation("developer", "yishuifengxiao");
		return oauth2Token;
	}

}
