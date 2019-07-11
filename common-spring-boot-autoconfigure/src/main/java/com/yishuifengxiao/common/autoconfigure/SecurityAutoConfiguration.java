package com.yishuifengxiao.common.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.encoder.impl.CustomPasswordEncoderImpl;
import com.yishuifengxiao.common.security.endpoint.ExceptionAuthenticationEntryPoint;
import com.yishuifengxiao.common.security.handle.CustomProcessor;
import com.yishuifengxiao.common.security.handle.impl.CustomProcessorImpl;
import com.yishuifengxiao.common.security.handler.CustomAccessDeniedHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationFailureHandler;
import com.yishuifengxiao.common.security.handler.CustomAuthenticationSuccessHandler;
import com.yishuifengxiao.common.security.handler.CustomLogoutSuccessHandler;
import com.yishuifengxiao.common.security.remerberme.CustomPersistentTokenRepository;
import com.yishuifengxiao.common.security.service.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.session.SessionInformationExpiredStrategyImpl;

@Configuration
@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityAutoConfiguration {

	/**
	 * 自定义属性配置
	 */
	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 注入自定义密码加密类
	 * 
	 * @return
	 */
	@Bean("passwordEncoder")
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new CustomPasswordEncoderImpl(securityProperties.getSecretKey());
	}

	/**
	 * 将密码加密类给注入进入
	 * 
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/**
	 * 注入用户查找配置类</br>
	 * 在系统没有注入UserDetailsService时，注册一个默认的UserDetailsService实例
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	public UserDetailsService userDetailsService() {
		return new CustomeUserDetailsServiceImpl(passwordEncoder());
	}

	/**
	 * 错误提示信息国际化
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		// messageSource.setBasenames("classpath:org/springframework/security/messages_zh_CN");
		messageSource.setBasenames("classpath*:com/yishuifengxiao/common/security/core/messages_zh_CN");
		// messageSource.setBasenames("classpath:messages_CN");
		return messageSource;
	}

	@Bean
	public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
		return new AcceptHeaderLocaleResolver();
	}

	/**
	 * 记住密码
	 * 
	 * @return
	 */
	@Bean("persistentTokenRepository")
	@ConditionalOnMissingBean(PersistentTokenRepository.class)
	public PersistentTokenRepository persistentTokenRepository() {
		return new CustomPersistentTokenRepository();
	}

	/**
	 * session 失效
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(SessionInformationExpiredStrategy.class)
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return new SessionInformationExpiredStrategyImpl();
	}
    
	/**
	 * 自定义处理
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
