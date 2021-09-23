package com.yishuifengxiao.common.social.weixin;

import org.springframework.social.connect.ConnectionFactory;

import com.yishuifengxiao.common.social.SocialProperties;
import com.yishuifengxiao.common.social.adapter.BaseSocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.social.weixin.connect.factoty.WechatConnectionFactory;

/**
 * 构建一个微信连接工厂
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class WechatAutoConfigurerAdapter extends BaseSocialAutoConfigurerAdapter {

	private SocialProperties socialProperties;

	@Override
	protected ConnectionFactory<?> createConnectionFactory() {
		return new WechatConnectionFactory(socialProperties.getWeixin().getProviderId(),
				socialProperties.getWeixin().getAppId(), socialProperties.getWeixin().getAppSecret());
	}

	public SocialProperties getSocialProperties() {
		return socialProperties;
	}

	public void setSocialProperties(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}

	public WechatAutoConfigurerAdapter(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}

	public WechatAutoConfigurerAdapter() {

	}

}