package com.yishuifengxiao.common.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.impl.FirewallWebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.impl.IgnoreResourceProvider;

/**
 * 配置系统中的web安全授权器(<code>WebSecurityProvider</code>)<br/><br/>
 * 
 * 主要是处理系统中所有的web安全性有关的配置
 * 
 * @author qingteng
 * @date 2020年10月26日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class  })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class WebSecurityAutoConfiguration {

	/**
	 * 忽视资源授权器<br/>
	 * 配置系统中需要忽视资源
	 * 
	 * @param securityProperties
	 * @return
	 */
	@Bean(name = "ignoreResourceProvider")
	@ConditionalOnMissingBean(name = { "ignoreResourceProvider" })
	public WebSecurityProvider ignoreResourceProvider(SecurityProperties securityProperties) {
		IgnoreResourceProvider ignoreResourceProvider = new IgnoreResourceProvider();
		return ignoreResourceProvider;
	}

	/**
	 * 默认实现的HttpFirewall，主要是解决路径里包含 // 路径报错的问题
	 * 
	 * @return
	 */
	@Bean("firewallWebSecurityProvider")
	@ConditionalOnMissingBean(name = { "firewallWebSecurityProvider" })
	public WebSecurityProvider firewallWebSecurityProvider() {
		return new FirewallWebSecurityProvider();
	}

}
