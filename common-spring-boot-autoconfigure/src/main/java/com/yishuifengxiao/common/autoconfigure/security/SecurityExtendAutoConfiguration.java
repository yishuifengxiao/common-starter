package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handle.CustomProcessor;
import com.yishuifengxiao.common.security.handle.impl.CustomProcessorImpl;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;

@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class })
public class SecurityExtendAutoConfiguration {

	/**
	 * 自定义属性配置
	 */
	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 自定义处理
	 * 
	 * @param objectMapper
	 * @return
	 */
	@Bean("customProcessor")
	@ConditionalOnMissingBean(name = "customProcessor")
	public CustomProcessor customProcessor(ObjectMapper objectMapper) {
		CustomProcessorImpl customHandle = new CustomProcessorImpl();
		customHandle.setObjectMapper(objectMapper);
		customHandle.setSecurityProperties(securityProperties);
		return customHandle;
	}

	/**
	 * 自定义登陆失败处理器
	 * 
	 * @return
	 */
	@Bean("formAuthenticationFailureHandler")
	@ConditionalOnMissingBean(name = "formAuthenticationFailureHandler")
	public AuthenticationFailureHandler formAuthenticationFailureHandler(CustomProcessor customHandle,
			ApplicationContext context) {
		CustomAuthenticationFailureHandler hanler = new CustomAuthenticationFailureHandler();
		hanler.setSecurityProperties(securityProperties);
		hanler.setCustomHandle(customHandle);
		hanler.setContext(context);
		return hanler;
	}

	/**
	 * 自定义登陆成功处理器
	 * 
	 * @return
	 */
	@Bean("formAuthenticationSuccessHandler")
	@ConditionalOnMissingBean(name = "formAuthenticationSuccessHandler")
	public AuthenticationSuccessHandler formAuthenticationSuccessHandler(CustomProcessor customHandle,
			ApplicationContext context) {
		CustomAuthenticationSuccessHandler hanler = new CustomAuthenticationSuccessHandler();
		hanler.setSecurityProperties(securityProperties);
		hanler.setCustomHandle(customHandle);
		hanler.setContext(context);
		return hanler;
	}

	/**
	 * 自定义退出成功处理器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(LogoutSuccessHandler.class)
	public LogoutSuccessHandler logoutSuccessHandler(CustomProcessor customHandle, ApplicationContext context) {
		CustomLogoutSuccessHandler hanler = new CustomLogoutSuccessHandler();
		hanler.setSecurityProperties(securityProperties);
		hanler.setCustomHandle(customHandle);
		hanler.setContext(context);
		return hanler;
	}

	/**
	 * 创建一个名字 exceptionAuthenticationEntryPoint 的token信息提示处理器
	 * 
	 * @return
	 */
	@Bean("exceptionAuthenticationEntryPoint")
	@ConditionalOnMissingBean(name = "exceptionAuthenticationEntryPoint")
	public AuthenticationEntryPoint exceptionAuthenticationEntryPoint(CustomProcessor customHandle,
			ApplicationContext context) {
		ExceptionAuthenticationEntryPoint point = new ExceptionAuthenticationEntryPoint();
		point.setCustomHandle(customHandle);
		point.setSecurityProperties(securityProperties);
		point.setContext(context);
		return point;
	}

	/**
	 * 权限拒绝处理器
	 * 
	 * @return
	 */
	@Bean("customAccessDeniedHandler")
	@ConditionalOnMissingBean(name = "customAccessDeniedHandler")
	public AccessDeniedHandler customAccessDeniedHandler(CustomProcessor customHandle, ApplicationContext context) {
		CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
		handler.setSecurityProperties(securityProperties);
		handler.setCustomHandle(customHandle);
		handler.setContext(context);
		return handler;
	}

}
