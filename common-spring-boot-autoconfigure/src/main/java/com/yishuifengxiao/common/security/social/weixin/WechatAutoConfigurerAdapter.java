package com.yishuifengxiao.common.security.social.weixin;

import org.springframework.social.connect.ConnectionFactory;

import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.social.adapter.SocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.weixin.connect.factoty.WechatConnectionFactory;
/**
 * 构建一个微信连接工厂
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class WechatAutoConfigurerAdapter extends SocialAutoConfigurerAdapter {
    
	private SocialProperties socialProperties;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter
     * #createConnectionFactory()
     */
    @Override
    protected ConnectionFactory<?> createConnectionFactory() {
        return new WechatConnectionFactory(socialProperties.getWeixin().getProviderId(), socialProperties.getWeixin().getAppId(), socialProperties.getWeixin().getAppSecret());
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