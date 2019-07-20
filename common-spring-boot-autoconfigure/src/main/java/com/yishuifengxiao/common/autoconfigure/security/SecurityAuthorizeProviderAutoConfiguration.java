package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.yishuifengxiao.common.security.authority.CustomAuthority;
import com.yishuifengxiao.common.security.authority.impl.CustomAuthorityImpl;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.CustomAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.FormLoginAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.InterceptAllAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.LoginOutAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.PermitAllAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.RemeberMeAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.SessionAuthorizeProvider;

@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
public class SecurityAuthorizeProviderAutoConfiguration {

	/**
	 * 自定义属性配置
	 */
	@Autowired
	protected SecurityProperties securityProperties;

	/**
	 * 表单登陆授权管理
	 * 
	 * @param authenticationSuccessHandler
	 * @param authenticationFailureHandler
	 * @return
	 */
	@Bean("formLoginProvider")
	@ConditionalOnMissingBean(name = "formLoginProvider")
	public AuthorizeProvider formLoginProvider(AuthenticationSuccessHandler authenticationSuccessHandler,
			AuthenticationFailureHandler authenticationFailureHandler) {
		FormLoginAuthorizeProvider formLoginProvider = new FormLoginAuthorizeProvider();
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
	@Bean("interceptAllProvider")
	@ConditionalOnMissingBean(name = "interceptAllProvider")
	public AuthorizeProvider interceptAllProvider() {
		return new InterceptAllAuthorizeProvider();
	}

	/**
	 * 退出授权管理
	 * 
	 * @param logoutSuccessHandler 退出成功管理
	 * @return
	 */
	@Bean("loginOutProvider")
	@ConditionalOnMissingBean(name = "loginOutProvider")
	public AuthorizeProvider loginOutProvider(LogoutSuccessHandler logoutSuccessHandler) {
		LoginOutAuthorizeProvider loginOutProvider = new LoginOutAuthorizeProvider();
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
	@Bean("remeberMeProvider")
	@ConditionalOnMissingBean(name = "remeberMeProvider")
	public AuthorizeProvider remeberMeProvider(PersistentTokenRepository persistentTokenRepository,
			UserDetailsService userDetailsService) {
		RemeberMeAuthorizeProvider remeberMeProvider = new RemeberMeAuthorizeProvider();
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
	@Bean("sessionProvider")
	@ConditionalOnMissingBean(name = "sessionProvider")
	public AuthorizeProvider sessionProvider(SessionInformationExpiredStrategy sessionInformationExpiredStrategy,
			AuthenticationFailureHandler authenticationFailureHandler) {
		SessionAuthorizeProvider sessionProvider = new SessionAuthorizeProvider();
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
	@Bean("permitAllProvider")
	@ConditionalOnMissingBean(name = "permitAllProvider")
	public AuthorizeProvider permitAllConfigProvider() {
		PermitAllAuthorizeProvider permitAllConfigProvider = new PermitAllAuthorizeProvider();
		permitAllConfigProvider.setSecurityProperties(securityProperties);
		return permitAllConfigProvider;
	}

	/**
	 * 注入一个名为 customAuthority 授权行为实体
	 * 
	 * @return
	 */
	@Bean("customAuthority")
	@ConditionalOnMissingBean(name = "customAuthority")
	public CustomAuthority customAuthority() {
		CustomAuthorityImpl customAuthority = new CustomAuthorityImpl();
		return customAuthority;
	}

	/**
	 * 自定义授权提供其
	 * 
	 * @param customAuthority
	 * @return
	 */
	@Bean("customAuthorizeProvider")
	@ConditionalOnMissingBean(name = "customAuthorizeProvider")
	public AuthorizeProvider customAuthorizeProvider(@Qualifier("customAuthority") CustomAuthority customAuthority) {
		CustomAuthorizeProvider customAuthorizeProvider = new CustomAuthorizeProvider();
		customAuthorizeProvider.setCustomAuthority(customAuthority);
		customAuthorizeProvider.setSecurityProperties(securityProperties);
		return customAuthorizeProvider;
	}

}
