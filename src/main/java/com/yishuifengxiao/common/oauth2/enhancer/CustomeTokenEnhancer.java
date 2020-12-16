package com.yishuifengxiao.common.oauth2.enhancer;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.yishuifengxiao.common.oauth2.Oauth2Server;
import com.yishuifengxiao.common.oauth2.entity.YishuiOAuth2AccessToken;
import com.yishuifengxiao.common.tool.encoder.DES;

/**
 * 自定义token生成器<br/>
 * 由<code> Oauth2Server </code>收集，然后经<code>public void configure(ResourceServerSecurityConfigurer resources) </code>注入到oauth2中
 * 
 * @see Oauth2Server
 * @author yishui
 * @date 2019年4月1日
 * @version 1.0.0
 */
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
	 * @return
	 */
	private YishuiOAuth2AccessToken getNewToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		YishuiOAuth2AccessToken oauth2Token = new YishuiOAuth2AccessToken(accessToken);
		// 获取到认证信息
		Authentication auth = authentication.getUserAuthentication();
		// 用户名
		String username = auth.getName();
		// 修改token
		oauth2Token.setValue(
				DES.encrypt(new StringBuffer(username).append(":").append(System.currentTimeMillis()).toString()));
		// 添加一个附加信息
		oauth2Token.addAdditionalInformation("developer", "yishuifengxiao");
		return oauth2Token;
	}

}
