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
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;

/**
 * 配置系统中的各种处理器
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class SecurityHandlerAutoConfiguration {

	/**
	 * 自定义登陆失败处理器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthenticationFailureHandler authenticationFailureHandler(HandlerProcessor handlerProcessor) {
		CustomAuthenticationFailureHandler hanler = new CustomAuthenticationFailureHandler();
		hanler.setHandlerProcessor(handlerProcessor);
		return hanler;
	}

	/**
	 * 自定义登陆成功处理器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthenticationSuccessHandler authenticationSuccessHandler(HandlerProcessor handlerProcessor,TokenBuilder tokenBuilder,SecurityProperties securityProperties) {
		CustomAuthenticationSuccessHandler hanler = new CustomAuthenticationSuccessHandler();
		hanler.setHandlerProcessor(handlerProcessor);
		hanler.setTokenBuilder(tokenBuilder);
		hanler.setSecurityProperties(securityProperties);
		return hanler;
	}

	/**
	 * 自定义退出成功处理器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(LogoutSuccessHandler.class)
	public LogoutSuccessHandler logoutSuccessHandler(HandlerProcessor handlerProcessor,TokenBuilder tokenBuilder) {
		CustomLogoutSuccessHandler hanler = new CustomLogoutSuccessHandler();
		hanler.setHandlerProcessor(handlerProcessor);
		hanler.setTokenBuilder(tokenBuilder);
		return hanler;
	}

	/**
	 * 创建一个名字 exceptionAuthenticationEntryPoint 的token信息提示处理器
	 * 
	 * @return
	 */
	@Bean("exceptionAuthenticationEntryPoint")
	@ConditionalOnMissingBean(name = "exceptionAuthenticationEntryPoint")
	public AuthenticationEntryPoint exceptionAuthenticationEntryPoint(HandlerProcessor handlerProcessor) {
		ExceptionAuthenticationEntryPoint point = new ExceptionAuthenticationEntryPoint();
		point.setHandlerProcessor(handlerProcessor);
		return point;
	}

	/**
	 * 权限拒绝处理器
	 * 
	 * @return
	 */
	@Bean("accessDeniedHandler")
	@ConditionalOnMissingBean(name = "accessDeniedHandler")
	public AccessDeniedHandler accessDeniedHandler(HandlerProcessor handlerProcessor) {
		CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
		handler.setHandlerProcessor(handlerProcessor);
		return handler;
	}

}
