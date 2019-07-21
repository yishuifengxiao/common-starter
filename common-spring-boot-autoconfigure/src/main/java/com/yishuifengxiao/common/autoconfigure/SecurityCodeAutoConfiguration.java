package com.yishuifengxiao.common.autoconfigure;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.adapter.AbstractSecurityAdapter;
import com.yishuifengxiao.common.security.adapter.impl.CodeValidateAdapter;
import com.yishuifengxiao.common.security.adapter.impl.SmsLoginAdapter;
import com.yishuifengxiao.common.security.filter.ValidateCodeFilter;
import com.yishuifengxiao.common.validation.CodeProcessorHolder;

/**
 * spring security验证码拦截应用配置
 *
 * @author yishui
 * @version 0.0.1
 * @date 2018年6月15日
 */
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security.code.filter")
public class SecurityCodeAutoConfiguration {

	/**
	 * 注入一个验证码过滤器
	 *
	 * @return
	 */
	@Bean("validateCodeFilter")
	@ConditionalOnMissingBean(name = "validateCodeFilter")
	public ValidateCodeFilter validateCodeFilter(AuthenticationFailureHandler authenticationFailureHandler,
			CodeProcessorHolder codeProcessorHolder, SecurityProperties securityProperties) {
		return new ValidateCodeFilter(authenticationFailureHandler, codeProcessorHolder, securityProperties);
	}

	/**
	 * 在spring security过滤器链中加入一个过滤器，用来进行验证码校验
	 *
	 * @param validateCodeFilter
	 * @return
	 */
	@Bean("codeValidateAdapter")
	@ConditionalOnBean(name = "validateCodeFilter")
	@ConditionalOnMissingBean(name = "codeValidateAdapter")
	public AbstractSecurityAdapter codeConfigAdapter(ValidateCodeFilter validateCodeFilter) {
		return new CodeValidateAdapter(validateCodeFilter);

	}

	/**
	 * 条件注入短信登录配置
	 *
	 * @return
	 */
	@Bean("smsLoginAdapter")
	@ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "smsLoginUrl")
	@ConditionalOnMissingBean(name = "smsLoginAdapter")
	@ConditionalOnBean(name = "smsUserDetailsService")
	public SmsLoginAdapter smsAuthenticationSecurityConfig(AuthenticationSuccessHandler authenticationFailureHandler,
			AuthenticationFailureHandler authenticationSuccessHandler,
			@Qualifier("smsUserDetailsService") UserDetailsService smsUserDetailsService,
			SecurityProperties securityProperties) {

		return new SmsLoginAdapter(authenticationFailureHandler, authenticationSuccessHandler, smsUserDetailsService,
				securityProperties.getCode().getSmsLoginUrl());
	}
}
