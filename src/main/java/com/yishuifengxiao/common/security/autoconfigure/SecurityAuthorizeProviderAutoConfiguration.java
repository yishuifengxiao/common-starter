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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.provider.custom.CustomResourceProvider;
import com.yishuifengxiao.common.security.provider.custom.impl.SimpleCustomResourceProvider;
import com.yishuifengxiao.common.security.provider.impl.CorsAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.CsrfAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.CustomAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.ExceptionAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.FormLoginAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.HttpBasicAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.InterceptAllAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.LoginOutAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.PermitAllAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.RemeberMeAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.SessionAuthorizeProvider;

/**
 * 配置spring security 授权提供器
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
public class SecurityAuthorizeProviderAutoConfiguration {

	/**
	 * 注入一个名为 customAuthority 授权行为实体
	 * 
	 * @return 自定义授权提供器
	 */
	@Bean("customAuthority")
	@ConditionalOnMissingBean(name = "customAuthority")
	public CustomResourceProvider customAuthority() {
		SimpleCustomResourceProvider customAuthority = new SimpleCustomResourceProvider();
		return customAuthority;
	}

	/**
	 * 自定义授权提供器
	 * 
	 * @param customAuthority 自定义授权提供器
	 * @return 授权提供器实例
	 */
	@Bean("customAuthorizeProvider")
	@ConditionalOnMissingBean(name = "customAuthorizeProvider")
	public AuthorizeProvider customAuthorizeProvider(
			@Qualifier("customAuthority") CustomResourceProvider customAuthority) {
		CustomAuthorizeProvider customAuthorizeProvider = new CustomAuthorizeProvider();
		customAuthorizeProvider.setCustomAuthority(customAuthority);
		return customAuthorizeProvider;
	}

	/**
	 * 表单登陆授权管理
	 * 
	 * @param authenticationSuccessHandler 登陆成功处理器
	 * @param authenticationFailureHandler 登陆失败处理器
	 * @return 授权提供器 实例
	 */
	@Bean("formLoginProvider")
	@ConditionalOnMissingBean(name = "formLoginProvider")
	public AuthorizeProvider formLoginProvider(AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler) {
		FormLoginAuthorizeProvider formLoginProvider = new FormLoginAuthorizeProvider();
		formLoginProvider.setFormAuthenticationFailureHandler(authenticationFailureHandler);
		formLoginProvider.setFormAuthenticationSuccessHandler(authenticationSuccessHandler);
		return formLoginProvider;
	}

	/**
	 * 拦截所有资源
	 * 
	 * @return 授权提供器 实例
	 */
	@Bean("interceptAllProvider")
	@ConditionalOnMissingBean(name = "interceptAllProvider")
	public AuthorizeProvider interceptAllProvider() {
		return new InterceptAllAuthorizeProvider();
	}

	/**
	 * 退出授权管理
	 * 
	 * @param logoutSuccessHandler 退出成功管理
	 * @return 授权提供器 实例
	 */
	@Bean("loginOutProvider")
	@ConditionalOnMissingBean(name = "loginOutProvider")
	public AuthorizeProvider loginOutProvider(LogoutSuccessHandler logoutSuccessHandler) {
		LoginOutAuthorizeProvider loginOutProvider = new LoginOutAuthorizeProvider();
		loginOutProvider.setCustomLogoutSuccessHandler(logoutSuccessHandler);
		return loginOutProvider;
	}

	/**
	 * 记住我授权管理
	 * 
	 * @param persistentTokenRepository token存储器
	 * @param userDetailsService        用户认证处理器
	 * @return 授权提供器 实例
	 */
	@Bean("remeberMeProvider")
	@ConditionalOnMissingBean(name = "remeberMeProvider")
	public AuthorizeProvider remeberMeProvider(PersistentTokenRepository persistentTokenRepository,
			UserDetailsService userDetailsService) {
		RemeberMeAuthorizeProvider remeberMeProvider = new RemeberMeAuthorizeProvider();
		remeberMeProvider.setPersistentTokenRepository(persistentTokenRepository);
		remeberMeProvider.setUserDetailsService(userDetailsService);
		return remeberMeProvider;
	}

	/**
	 * session授权管理
	 * 
	 * @param sessionInformationExpiredStrategy session失效策略
	 * @param authenticationFailureHandler      认证失败处理器
	 * @return 授权提供器 实例
	 */
	@Bean("sessionProvider")
	@ConditionalOnMissingBean(name = "sessionProvider")
	public AuthorizeProvider sessionProvider(SessionInformationExpiredStrategy sessionInformationExpiredStrategy,
			AuthenticationFailureHandler authenticationFailureHandler) {
		SessionAuthorizeProvider sessionProvider = new SessionAuthorizeProvider();
		sessionProvider.setCustomAuthenticationFailureHandler(authenticationFailureHandler);
		sessionProvider.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
		return sessionProvider;
	}

	/**
	 * 放行通过授权管理
	 * 
	 * @return 授权提供器 实例
	 */
	@Bean("permitAllProvider")
	@ConditionalOnMissingBean(name = "permitAllProvider")
	public AuthorizeProvider permitAllConfigProvider() {
		PermitAllAuthorizeProvider permitAllConfigProvider = new PermitAllAuthorizeProvider();
		return permitAllConfigProvider;
	}

	/**
	 * Basic登陆授权提供器
	 * 
	 * @param exceptionAuthenticationEntryPoint 异常处理
	 * @return 授权提供器 实例
	 */
	@Bean("httpBasicAuthorizeProvider")
	@ConditionalOnMissingBean(name = "httpBasicAuthorizeProvider")
	public AuthorizeProvider httpBasicAuthorizeProvider(
			@Qualifier("exceptionAuthenticationEntryPoint") AuthenticationEntryPoint exceptionAuthenticationEntryPoint) {
		HttpBasicAuthorizeProvider httpBasicAuthorizeProvider = new HttpBasicAuthorizeProvider();
		httpBasicAuthorizeProvider.setExceptionAuthenticationEntryPoint(exceptionAuthenticationEntryPoint);
		return httpBasicAuthorizeProvider;
	}

	/**
	 * 异常处理授权提供器
	 * 
	 * @param exceptionAuthenticationEntryPoint  AuthenticationEntryPoint
	 * @param customAccessDeniedHandler 权限拒绝处理器
	 * @return 授权提供器 实例
	 */
	@Bean("exceptionAuthorizeProvider")
	@ConditionalOnMissingBean(name = "exceptionAuthorizeProvider")
	public AuthorizeProvider exceptionAuthorizeProvider(
			@Qualifier("exceptionAuthenticationEntryPoint") AuthenticationEntryPoint exceptionAuthenticationEntryPoint,
			AccessDeniedHandler customAccessDeniedHandler) {
		ExceptionAuthorizeProvider exceptionAuthorizeProvider = new ExceptionAuthorizeProvider();
		exceptionAuthorizeProvider.setExceptionAuthenticationEntryPoint(exceptionAuthenticationEntryPoint);
		exceptionAuthorizeProvider.setCustomAccessDeniedHandler(customAccessDeniedHandler);
		return exceptionAuthorizeProvider;
	}

	/**
	 * 跨域处理授权提供器
	 * 
	 * @return 授权提供器 实例
	 */
	@Bean("corsAuthorizeProvider")
	@ConditionalOnMissingBean(name = "corsAuthorizeProvider")
	public AuthorizeProvider corsAuthorizeProvider() {
		CorsAuthorizeProvider corsAuthorizeProvider = new CorsAuthorizeProvider();
		return corsAuthorizeProvider;
	}

	/**
	 * CSRF处理授权提供器
	 * 
	 * @return 授权提供器 实例
	 */
	@Bean("csrfAuthorizeProvider")
	@ConditionalOnMissingBean(name = "csrfAuthorizeProvider")
	public AuthorizeProvider csrfAuthorizeProvider() {
		CsrfAuthorizeProvider csrfAuthorizeProvider = new CsrfAuthorizeProvider();
		return csrfAuthorizeProvider;
	}

}
