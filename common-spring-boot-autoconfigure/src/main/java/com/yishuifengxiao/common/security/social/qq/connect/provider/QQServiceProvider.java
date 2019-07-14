package com.yishuifengxiao.common.security.social.qq.connect.provider;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.yishuifengxiao.common.security.social.qq.api.QQ;
import com.yishuifengxiao.common.security.social.qq.api.impl.QQImpl;
import com.yishuifengxiao.common.security.social.qq.connect.QQOAuth2Template;

public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {// 获取code
	private static final String QQ_URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

	// 获取access_token 也就是令牌
	private static final String QQ_URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

	private String appId;

	public QQServiceProvider(String appId, String appSecret) {
		super(new QQOAuth2Template(appId, appSecret, QQ_URL_AUTHORIZE, QQ_URL_ACCESS_TOKEN));
		this.appId = appId;
	}

	public QQ getApi(String accessToken) {
		// 保证多例，不能单例
		return new QQImpl(accessToken, appId);
	}
}