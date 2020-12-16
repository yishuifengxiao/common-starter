package com.yishuifengxiao.common.social.qq;

import org.springframework.social.connect.ConnectionFactory;

import com.yishuifengxiao.common.social.adapter.BaseSocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.social.qq.connect.factory.QqConnectionFactory;

/**
 * 构建QQ登陆连接工厂
 * 
 * @author yishui
 * @date 2019年7月15日
 * @version 1.0.0
 */
public class QqSocialAutoConfigurerAdapter extends BaseSocialAutoConfigurerAdapter {

	private String qqAppId;

	private String qqAppSecret;

	private String qqProviderId;

	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		return new QqConnectionFactory(qqProviderId, qqAppId, qqAppSecret);
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

	public QqSocialAutoConfigurerAdapter(String qqAppId, String qqAppSecret, String qqProviderId) {
		this.qqAppId = qqAppId;
		this.qqAppSecret = qqAppSecret;
		this.qqProviderId = qqProviderId;
	}

	public QqSocialAutoConfigurerAdapter() {

	}

}