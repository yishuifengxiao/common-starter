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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.extractor.SecurityExtractor;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;

/**
 * 配置系统中的各种处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class SecurityHandlerAutoConfiguration {

	/**
	 * 登陆失败处理器
	 * 
	 * @param handlerProcessor 协助处理器
	 * @param propertyResource 资源管理器
	 * @return 登陆失败处理器
	 */
	@Bean
	@ConditionalOnMissingBean({ AuthenticationFailureHandler.class })
	public AuthenticationFailureHandler authenticationFailureHandler(HandlerProcessor handlerProcessor,
			PropertyResource propertyResource) {
		CustomAuthenticationFailureHandler hanler = new CustomAuthenticationFailureHandler();
		hanler.setHandlerProcessor(handlerProcessor);
		hanler.setPropertyResource(propertyResource);
		return hanler;
	}

	/**
	 * 配置登陆成功处理器
	 * 
	 * @param handlerProcessor  协助处理器
	 * @param securityHelper
	 * @param propertyResource  资源管理器
	 * @param securityExtractor 信息提取器
	 * @return 登陆成功处理器
	 */
	@Bean
	@ConditionalOnMissingBean({ AuthenticationSuccessHandler.class })
	public AuthenticationSuccessHandler authenticationSuccessHandler(HandlerProcessor handlerProcessor,
			SecurityHelper securityHelper, PropertyResource propertyResource, SecurityExtractor securityExtractor) {

		CustomAuthenticationSuccessHandler hanler = new CustomAuthenticationSuccessHandler(handlerProcessor,
				securityHelper, propertyResource, securityExtractor);

		return hanler;
	}

	/**
	 * 退出成功处理器
	 * 
	 * @param handlerProcessor 协助处理器
	 * @param tokenBuilder     token生成器
	 * @param propertyResource 资源管理器
	 * @return 退出成功处理器
	 */
	@Bean
	@ConditionalOnMissingBean({ LogoutSuccessHandler.class })
	public LogoutSuccessHandler logoutSuccessHandler(HandlerProcessor handlerProcessor, TokenBuilder tokenBuilder,
			PropertyResource propertyResource) {
		CustomLogoutSuccessHandler hanler = new CustomLogoutSuccessHandler();
		hanler.setHandlerProcessor(handlerProcessor);
		hanler.setPropertyResource(propertyResource);
		hanler.setTokenBuilder(tokenBuilder);
		return hanler;
	}

	/**
	 * 资源异常处理器
	 * 
	 * @param handlerProcessor 协助处理器
	 * @param propertyResource 资源管理器
	 * @return 资源异常处理器
	 */
	@Bean("exceptionAuthenticationEntryPoint")
	@ConditionalOnMissingBean(name = "exceptionAuthenticationEntryPoint")
	public AuthenticationEntryPoint exceptionAuthenticationEntryPoint(HandlerProcessor handlerProcessor,
			PropertyResource propertyResource) {
		ExceptionAuthenticationEntryPoint point = new ExceptionAuthenticationEntryPoint();
		point.setHandlerProcessor(handlerProcessor);
		point.setPropertyResource(propertyResource);
		return point;
	}

	/**
	 * 权限拒绝处理器
	 * 
	 * @param handlerProcessor 协助处理器
	 * @param propertyResource 资源管理器
	 * @return 权限拒绝处理器
	 */
	@Bean
	@ConditionalOnMissingBean({ AccessDeniedHandler.class })
	public AccessDeniedHandler accessDeniedHandler(HandlerProcessor handlerProcessor,
			PropertyResource propertyResource) {
		CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
		handler.setHandlerProcessor(handlerProcessor);
		handler.setPropertyResource(propertyResource);
		return handler;
	}

}
