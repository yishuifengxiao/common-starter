package com.yishuifengxiao.common.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.security.oauth2.translator.Auth2ResponseExceptionTranslator;

@Configuration
@ConditionalOnClass({ EnableResourceServer.class })
@ConditionalOnWebApplication
@ConditionalOnBean(ResourceServerConfiguration.class)
@AutoConfigureBefore({ SecurityAutoConfiguration.class, SecurityAutoConfiguration.class })
@EnableConfigurationProperties(Oauth2Properties.class)
public class Oauth2ResourceAutoConfiguration extends ResourceServerConfigurerAdapter {

	@Autowired
	private Oauth2Properties oauth2Properties;

	@Autowired
	private DefaultWebSecurityExpressionHandler expressionHandler;
	/**
	 * 定义在security-core包中
	 */
	@Autowired
	private AccessDeniedHandler customAccessDeniedHandler;

	/**
	 * 定义在security-core包中
	 */
	@Autowired
	@Qualifier("exceptionAuthenticationEntryPoint")
	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {

		// 定义异常转换类生效
		AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint)
				.setExceptionTranslator(new Auth2ResponseExceptionTranslator());
		resources.authenticationEntryPoint(authenticationEntryPoint);
		resources.resourceId(this.oauth2Properties.getRealm()).expressionHandler(expressionHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//@formatter:off  
		
		http
			.exceptionHandling()
			.authenticationEntryPoint(exceptionAuthenticationEntryPoint)// 定义的不存在access_token时候响应
			.accessDeniedHandler(customAccessDeniedHandler)//自定义权限拒绝处理器
			;
		//@formatter:on  
	}

}
