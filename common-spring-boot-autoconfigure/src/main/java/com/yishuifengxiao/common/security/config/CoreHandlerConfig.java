package com.yishuifengxiao.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handle.CustomHandle;
import com.yishuifengxiao.common.security.handle.impl.CustomHandleImpl;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;

/**
 * 自定义处理器的相关配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
@Configuration
public class CoreHandlerConfig {
	/**
	 * 自定义属性配置
	 */
	@Autowired
	private SecurityProperties securityProperties;
	/**
	 * 对象映射器
	 */
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ApplicationContext context;

	@Bean("customHandle")
	@ConditionalOnMissingBean(name = "customHandle")
	public CustomHandle customHandle() {
		CustomHandleImpl customHandle = new CustomHandleImpl();
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
	public AuthenticationFailureHandler formAuthenticationFailureHandler(CustomHandle customHandle) {
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
	public AuthenticationSuccessHandler formAuthenticationSuccessHandler(CustomHandle customHandle) {
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
	public LogoutSuccessHandler logoutSuccessHandler(CustomHandle customHandle) {
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
	public AuthenticationEntryPoint exceptionAuthenticationEntryPoint(CustomHandle customHandle) {
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
	public AccessDeniedHandler customAccessDeniedHandler(CustomHandle customHandle) {
		CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
		handler.setSecurityProperties(securityProperties);
		handler.setCustomHandle(customHandle);
		handler.setContext(context);
		return handler;
	}
}
