package com.yishuifengxiao.common.social.qq.connect.factory;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.yishuifengxiao.common.social.qq.adapter.QqAdapter;
import com.yishuifengxiao.common.social.qq.api.QQ;
import com.yishuifengxiao.common.social.qq.connect.provider.QqServiceProvider;

/**
 * QQ登陆连接工厂
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class QqConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	public QqConnectionFactory(String providerId, String appId, String appSecret) {
		// 这样的 访问/auth/{providerId的实际值} 这个连接即可开始QQ登录进程
		super(providerId, new QqServiceProvider(appId, appSecret), new QqAdapter());

	}

}