package com.yishuifengxiao.common.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.social.adapter.SocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.qq.QQSocialAutoConfigurerAdapter;

@Configuration
@EnableConfigurationProperties(SocialProperties.class)
public class SpringSocialAutoConfiguration {
	
	@Bean
	public SocialAutoConfigurerAdapter qQSocialAutoConfigurerAdapter(SocialProperties socialProperties){
		
		QQSocialAutoConfigurerAdapter qQSocialAutoConfigurerAdapter=new QQSocialAutoConfigurerAdapter();
		qQSocialAutoConfigurerAdapter.setQqAppId(socialProperties.getQq().getAppId());
		qQSocialAutoConfigurerAdapter.setQqAppSecret(socialProperties.getQq().getAppSecuret());
		qQSocialAutoConfigurerAdapter.setQqProviderId(socialProperties.getQq().getProviderId());
		return qQSocialAutoConfigurerAdapter ;
	}
	


}
