package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.provider.AuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.FormLoginAuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.InterceptAllAuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.LoginOutAuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.PermitAllAuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.RemeberMeAuthorizeConfigProvider;
import com.yishuifengxiao.common.security.provider.impl.SessionAuthorizeConfigProvider;

@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
	WebSecurityConfigurerAdapter.class })
public class SecurityAuthorizeAutoConfiguration {

	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;

	/**
	 * 表单登陆授权管理
	 * 
	 * @param formAuthenticationSuccessHandler
	 * @param formAuthenticationFailureHandler
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider formLoginProvider(AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler) {
		FormLoginAuthorizeConfigProvider formLoginProvider = new FormLoginAuthorizeConfigProvider();
		formLoginProvider.setFormAuthenticationFailureHandler(authenticationFailureHandler);
		formLoginProvider.setFormAuthenticationSuccessHandler(authenticationSuccessHandler);
		formLoginProvider.setSecurityProperties(securityProperties);
		return formLoginProvider;
	}

	/**
	 * 拦截所有资源
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider interceptAllProvider() {
		return new InterceptAllAuthorizeConfigProvider();
	}

	/**
	 * 退出授权管理
	 * 
	 * @param logoutSuccessHandler 退出成功管理
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider loginOutProvider(LogoutSuccessHandler logoutSuccessHandler) {
		LoginOutAuthorizeConfigProvider loginOutProvider = new LoginOutAuthorizeConfigProvider();
		loginOutProvider.setCustomLogoutSuccessHandler(logoutSuccessHandler);
		loginOutProvider.setSecurityProperties(securityProperties);
		return loginOutProvider;
	}

	/**
	 * 记住我授权管理
	 * 
	 * @param persistentTokenRepository token存储器
	 * @param userDetailsService        用户认证处理器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider remeberMeProvider(PersistentTokenRepository persistentTokenRepository,
			UserDetailsService userDetailsService) {
		RemeberMeAuthorizeConfigProvider remeberMeProvider = new RemeberMeAuthorizeConfigProvider();
		remeberMeProvider.setPersistentTokenRepository(persistentTokenRepository);
		remeberMeProvider.setSecurityProperties(securityProperties);
		remeberMeProvider.setUserDetailsService(userDetailsService);
		return remeberMeProvider;
	}

	/**
	 * session授权管理
	 * 
	 * @param sessionInformationExpiredStrategy session失效策略
	 * @param authenticationFailureHandler      认证失败处理器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider sessionProvider(SessionInformationExpiredStrategy sessionInformationExpiredStrategy,
			AuthenticationFailureHandler authenticationFailureHandler) {
		SessionAuthorizeConfigProvider sessionProvider = new SessionAuthorizeConfigProvider();
		sessionProvider.setCustomAuthenticationFailureHandler(authenticationFailureHandler);
		sessionProvider.setSecurityProperties(securityProperties);
		sessionProvider.setSessionInformationExpiredStrategy(sessionInformationExpiredStrategy);
		return sessionProvider;
	}

	/**
	 * 放行通过授权管理
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigProvider permitAllConfigProvider() {
		PermitAllAuthorizeConfigProvider permitAllConfigProvider = new PermitAllAuthorizeConfigProvider();
		permitAllConfigProvider.setSecurityProperties(securityProperties);
		return permitAllConfigProvider;
	}

}
