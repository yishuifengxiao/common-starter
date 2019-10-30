package com.yishuifengxiao.common.security.social.qq.connect.factory;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

import com.yishuifengxiao.common.security.social.qq.adapter.QQAdapter;
import com.yishuifengxiao.common.security.social.qq.api.QQ;
import com.yishuifengxiao.common.security.social.qq.connect.provider.QQServiceProvider;
/**
 * QQ登陆连接工厂
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

	public QQConnectionFactory(String providerId, String appId,String appSecret ) {
		//这样的 访问/auth/{providerId的实际值} 这个连接即可开始QQ登录进程
		super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
		
	}

}