package com.yishuifengxiao.common.security.autoconfigure;

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

import com.yishuifengxiao.common.code.processor.CodeProcessor;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.filter.ValidateCodeFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.CodeValidateInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.SmsLoginInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;

/**
 * 1. 配置系统中验证码拦截功能<br/>
 * 2. 配置短信验证码登陆功能<br/>
 *
 * @author yishui
 * @version 0.0.1
 * @date 2018年6月15日
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class SecurityCodeAutoConfiguration {

	/**
	 * 注入一个验证码过滤器
	 * 
	 * @param codeProcessor
	 * @param securityProperties
	 * @param handlerProcessor
	 * @return
	 */
	@Bean("validateCodeFilter")
	@ConditionalOnMissingBean(name = "validateCodeFilter")
	@ConditionalOnBean(name = "codeProcessor")
	public ValidateCodeFilter validateCodeFilter(CodeProcessor codeProcessor, SecurityProperties securityProperties,
			HandlerProcessor handlerProcessor) {
		ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
		validateCodeFilter.setCodeProcessor(codeProcessor);
		validateCodeFilter.setSecurityProperties(securityProperties);
		validateCodeFilter.setHandlerProcessor(handlerProcessor);
		return validateCodeFilter;
	}

	/**
	 * 在spring security过滤器链中加入一个过滤器，用来进行验证码校验
	 *
	 * @param codeValidateInterceptor
	 * @return
	 */
	@Bean("codeValidateInterceptor")
	@ConditionalOnMissingBean(name = "codeValidateInterceptor")
	@ConditionalOnBean(name = "validateCodeFilter")
	public HttpSecurityInterceptor codeValidateInterceptor(ValidateCodeFilter validateCodeFilter) {
		return new CodeValidateInterceptor(validateCodeFilter);

	}

	/**
	 * 条件注入短信登录配置
	 *
	 * @return
	 */

	/**
	 * 配置短信验证码登陆功能<br/>
	 * 要想使短信验证码功能生效，需要配置：<br/>
	 * 1 先配置一个短信登陆地址属性(<code>yishuifengxiao.security.code.sms-login-url</code>)<br/>
	 * 2 再配置一个名为 smsUserDetailsService 的 <code>UserDetailsService</code> 实例
	 * 
	 * @param authenticationFailureHandler
	 * @param authenticationSuccessHandler
	 * @param smsUserDetailsService
	 * @param securityProperties
	 * @return
	 */
	@Bean("smsLoginInterceptor")
	@ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "sms-login-url")
	@ConditionalOnMissingBean(name = "smsLoginInterceptor")
	@ConditionalOnBean(name = "smsUserDetailsService")
	public HttpSecurityInterceptor smsLoginInterceptor(AuthenticationSuccessHandler authenticationFailureHandler,
			AuthenticationFailureHandler authenticationSuccessHandler,
			@Qualifier("smsUserDetailsService") UserDetailsService smsUserDetailsService,
			SecurityProperties securityProperties) {

		return new SmsLoginInterceptor(authenticationFailureHandler, authenticationSuccessHandler,
				smsUserDetailsService, securityProperties.getCode().getSmsLoginUrl());
	}
}
