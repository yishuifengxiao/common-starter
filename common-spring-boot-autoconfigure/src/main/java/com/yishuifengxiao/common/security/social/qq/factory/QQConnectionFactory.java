package com.yishuifengxiao.common.security.social.qq.factory;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.yishuifengxiao.common.security.social.qq.adapter.QQAdapter;
import com.yishuifengxiao.common.security.social.qq.api.QQ;
import com.yishuifengxiao.common.security.social.qq.provider.QQServiceProvider;

public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	public QQConnectionFactory(String providerId, String appId,String appSecret ) {
		//这样的 访问/auth/{providerId的实际值} 这个连接即可开始QQ登录进程
		super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
		
	}

}