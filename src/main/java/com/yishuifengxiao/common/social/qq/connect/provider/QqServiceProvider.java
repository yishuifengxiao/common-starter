package com.yishuifengxiao.common.social.qq.connect.provider;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

import com.yishuifengxiao.common.social.qq.api.QQ;
import com.yishuifengxiao.common.social.qq.api.impl.QqImpl;
import com.yishuifengxiao.common.social.qq.connect.QqOauth2Template;

/**
 * 构建 QQServiceProvider 以提供给 factory 生成 connection
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class QqServiceProvider extends AbstractOAuth2ServiceProvider<QQ> {
	/**
	 * 获取code 的地址
	 */
	private static final String QQ_URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

	/**
	 * 获取access_token 也就是令牌  的地址
	 */
	private static final String QQ_URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

	private String appId;

	public QqServiceProvider(String appId, String appSecret) {
		super(new QqOauth2Template(appId, appSecret, QQ_URL_AUTHORIZE, QQ_URL_ACCESS_TOKEN));
		this.appId = appId;
	}

	@Override
	public QQ getApi(String accessToken) {
		// 保证多例，不能单例
		return new QqImpl(accessToken, appId);
	}
}