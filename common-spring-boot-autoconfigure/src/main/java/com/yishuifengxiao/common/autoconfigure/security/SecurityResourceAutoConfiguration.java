package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.authorize.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.authorize.custom.impl.DefaultCustomResourceProvider;
import com.yishuifengxiao.common.security.authorize.ignore.DefaultIgnoreResourceProvider;
import com.yishuifengxiao.common.security.authorize.ignore.IgnoreResourceProvider;
import com.yishuifengxiao.common.security.authorize.intercept.AuthorizeResourceProvider;
import com.yishuifengxiao.common.security.authorize.intercept.DefaultAuthorizeResourceProvider;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.CustomAuthorizeProvider;

/**
 * 授权资源配置
 * @author yishui
 * @date 2019年11月7日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
public class SecurityResourceAutoConfiguration {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;
	
	/**
	 * 授权资源配置器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeResourceProvider authorizeResourceProvider(Oauth2Properties oauth2Properties,
			SecurityProperties securityProperties, SocialProperties socialProperties) {
		DefaultAuthorizeResourceProvider authorizeResourceProvider = new DefaultAuthorizeResourceProvider();
		authorizeResourceProvider.setOauth2Properties(oauth2Properties);
		authorizeResourceProvider.setSecurityProperties(securityProperties);
		authorizeResourceProvider.setSocialProperties(socialProperties);
		return authorizeResourceProvider;
	}

	/**
	 * 配置需要忽视的资源
	 * 
	 * @param securityProperties
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public IgnoreResourceProvider ignoreResourceProvider(SecurityProperties securityProperties) {
		DefaultIgnoreResourceProvider ignoreResourceProvider = new DefaultIgnoreResourceProvider();
		ignoreResourceProvider.setSecurityProperties(securityProperties);
		return ignoreResourceProvider;
	}
	
	/**
	 * 注入一个名为 customAuthority 授权行为实体
	 * 
	 * @return
	 */
	@Bean("customAuthority")
	@ConditionalOnMissingBean(name = "customAuthority")
	public CustomResourceProvider customAuthority() {
		DefaultCustomResourceProvider customAuthority = new DefaultCustomResourceProvider();
		return customAuthority;
	}

	/**
	 * 自定义授权提供器
	 * 
	 * @param customAuthority
	 * @return
	 */
	@Bean("customAuthorizeProvider")
	@ConditionalOnMissingBean(name = "customAuthorizeProvider")
	public AuthorizeProvider customAuthorizeProvider(@Qualifier("customAuthority") CustomResourceProvider customAuthority) {
		CustomAuthorizeProvider customAuthorizeProvider = new CustomAuthorizeProvider();
		customAuthorizeProvider.setCustomAuthority(customAuthority);
		customAuthorizeProvider.setSecurityProperties(securityProperties);
		return customAuthorizeProvider;
	}

}
