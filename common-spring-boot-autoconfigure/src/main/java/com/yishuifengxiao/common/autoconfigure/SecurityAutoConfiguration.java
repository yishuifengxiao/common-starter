package com.yishuifengxiao.common.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.yishuifengxiao.common.autoconfigure.security.PersistentTokenAutoConfiguration;
import com.yishuifengxiao.common.autoconfigure.security.SecurityAuthorizeProviderAutoConfiguration;
import com.yishuifengxiao.common.autoconfigure.security.SecurityCodeAutoConfiguration;
import com.yishuifengxiao.common.autoconfigure.security.SecurityHandlerAutoConfiguration;
import com.yishuifengxiao.common.autoconfigure.security.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.autoconfigure.security.SecurityResourceAutoConfiguration;
import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.adapter.AbstractSecurityAdapter;
import com.yishuifengxiao.common.security.authorize.ignore.IgnoreResourceProvider;
import com.yishuifengxiao.common.security.encoder.impl.SimpleBasePasswordEncoder;
import com.yishuifengxiao.common.security.manager.DefaultSecurityContextManager;
import com.yishuifengxiao.common.security.manager.SecurityContextManager;
import com.yishuifengxiao.common.security.manager.adapter.AdapterManager;
import com.yishuifengxiao.common.security.manager.adapter.DefaultAdapterManager;
import com.yishuifengxiao.common.security.manager.authorize.AuthorizeConfigManager;
import com.yishuifengxiao.common.security.manager.authorize.DefaultAuthorizeConfigManager;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.remerberme.InMemoryTokenRepositoryImpl;
import com.yishuifengxiao.common.security.service.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.session.SessionInformationExpiredStrategyImpl;
import com.yishuifengxiao.common.security.websecurity.DefaultWebSecurityManager;
import com.yishuifengxiao.common.security.websecurity.WebSecurityManager;
import com.yishuifengxiao.common.security.websecurity.adapter.DefaultWebSecurityAdapter;
import com.yishuifengxiao.common.security.websecurity.adapter.WebSecurityAdapter;

/**
 * spring security应用配置
 * 
 * @author yishui
 * @date 2018年6月15日
 * @version 0.0.1
 */
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@EnableConfigurationProperties({ SecurityProperties.class, Oauth2Properties.class, SocialProperties.class })
@Import({ SecurityHandlerAutoConfiguration.class, SecurityAuthorizeProviderAutoConfiguration.class,
		PersistentTokenAutoConfiguration.class, SecurityCodeAutoConfiguration.class,
		SecurityRedisAutoConfiguration.class ,SecurityResourceAutoConfiguration.class})
public class SecurityAutoConfiguration {

	/**
	 * 注入自定义密码加密类
	 * 
	 * @return
	 */
	@Bean("passwordEncoder")
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder(SecurityProperties securityProperties) {
		return new SimpleBasePasswordEncoder(securityProperties.getSecretKey());
	}

	/**
	 * 注入用户查找配置类</br>
	 * 在系统没有注入UserDetailsService时，注册一个默认的UserDetailsService实例
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return new CustomeUserDetailsServiceImpl(passwordEncoder);
	}

	/**
	 * 将密码加密类注入到spring security中
	 * 
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return authenticationProvider;
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
		messageSource.setBasenames("classpath*:messages_zh_CN");
		// messageSource.setBasenames("classpath:messages_CN");
		return messageSource;
	}

	/**
	 * 错误提示国际化
	 * 
	 * @return
	 */
	@Bean
	public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
		return new AcceptHeaderLocaleResolver();
	}

	/**
	 * 记住密码策略【存储内存中在redis数据库中】
	 * 
	 * @return
	 */
	@Bean("persistentTokenRepository")
	@ConditionalOnMissingBean(name = { "redisTemplate", "persistentTokenRepository" })
	public PersistentTokenRepository inMemoryTokenRepository() {
		return new InMemoryTokenRepositoryImpl();
	}

	/**
	 * session 失效策略，可以在此方法中记录谁把谁的登陆状态挤掉
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return new SessionInformationExpiredStrategyImpl();
	}

	/**
	 * 授权管理器
	 * 
	 * @param authorizeProviders 系统中所有的授权提供器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AuthorizeConfigManager authorizeConfigManager(List<AuthorizeProvider> authorizeProviders) {
		DefaultAuthorizeConfigManager authorizeConfigManager = new DefaultAuthorizeConfigManager();
		authorizeConfigManager.setAuthorizeConfigProviders(authorizeProviders);
		return authorizeConfigManager;
	}

	/**
	 * 自定义适配器管理器
	 * 
	 * @param securityAdapters 系统中所有的权限适配器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public AdapterManager adapterManager(List<AbstractSecurityAdapter> securityAdapters) {
		DefaultAdapterManager defaultAdapterManager = new DefaultAdapterManager();
		defaultAdapterManager.setSecurityAdapters(securityAdapters);
		return defaultAdapterManager;
	}

	/**
	 * 自定义授权管理器
	 * 
	 * @param authorizeConfigManager 授权管理器
	 * @param adapterManager         自定义适配器管理器
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SecurityContextManager securityContextManager(AuthorizeConfigManager authorizeConfigManager,
			AdapterManager adapterManager) {
		DefaultSecurityContextManager securityContextManager = new DefaultSecurityContextManager();
		securityContextManager.setAdapterManager(adapterManager);
		securityContextManager.setAuthorizeConfigManager(authorizeConfigManager);
		return securityContextManager;
	}

	/**
	 * WebSecurity 管理器
	 * 
	 * @param ignoreResourcesConfig
	 * @param webSecurityAdapters
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public WebSecurityManager webSecurityManager(IgnoreResourceProvider ignoreResourceProvider,
			List<WebSecurityAdapter> webSecurityAdapters) {
		DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
		webSecurityManager.setIgnoreResourceProvider(ignoreResourceProvider);
		webSecurityManager.setWebSecurityAdapters(webSecurityAdapters);
		return webSecurityManager;
	}

	/**
	 * 默认实现的HttpFirewall，主要是解决路径里包含 // 路径报错的问题
	 * 
	 * @return
	 */
	@Bean("firewall")
	@ConditionalOnMissingBean(name = { "firewall" })
	public WebSecurityAdapter fireWell() {
		return new DefaultWebSecurityAdapter();
	}

}
