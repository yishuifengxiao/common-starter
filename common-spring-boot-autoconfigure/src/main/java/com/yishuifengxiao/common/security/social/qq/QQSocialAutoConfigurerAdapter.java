package com.yishuifengxiao.common.security.social.qq;

import org.springframework.social.connect.ConnectionFactory;

import com.yishuifengxiao.common.security.social.adapter.SocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.qq.connect.factory.QQConnectionFactory;

/**
 * 构建QQ登陆连接工厂
 * 
 * @author yishui
 * @date 2019年7月15日
 * @version 1.0.0
 */
public class QQSocialAutoConfigurerAdapter extends SocialAutoConfigurerAdapter {

	private String qqAppId;

	private String qqAppSecret;

	private String qqProviderId;

	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		return new QQConnectionFactory(qqProviderId, qqAppId, qqAppSecret);
	}

	public String getQqAppId() {
		return qqAppId;
	}

	public void setQqAppId(String qqAppId) {
		this.qqAppId = qqAppId;
	}

	public String getQqAppSecret() {
		return qqAppSecret;
	}

	public void setQqAppSecret(String qqAppSecret) {
		this.qqAppSecret = qqAppSecret;
	}

	public String getQqProviderId() {
		return qqProviderId;
	}

	public void setQqProviderId(String qqProviderId) {
		this.qqProviderId = qqProviderId;
	}

	public QQSocialAutoConfigurerAdapter(String qqAppId, String qqAppSecret, String qqProviderId) {
		this.qqAppId = qqAppId;
		this.qqAppSecret = qqAppSecret;
		this.qqProviderId = qqProviderId;
	}

	public QQSocialAutoConfigurerAdapter() {

	}

}