package com.yishuifengxiao.common.security;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

import com.yishuifengxiao.common.security.authorize.SecurityContextManager;
import com.yishuifengxiao.common.security.authorize.SimpleSecurityContextManager;
import com.yishuifengxiao.common.security.autoconfigure.HttpSecurityAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityAuthorizeProviderAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityCodeAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityHandlerAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.WebSecurityAutoConfiguration;
import com.yishuifengxiao.common.security.encoder.impl.SimpleBasePasswordEncoder;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.processor.impl.SimpleHandlerProcessor;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.remerberme.InMemoryTokenRepositoryImpl;
import com.yishuifengxiao.common.security.resource.SimplePropertyResource;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.service.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.session.SessionInformationExpiredStrategyImpl;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;
import com.yishuifengxiao.common.social.SocialProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * spring security应用配置
 * 
 * @author yishui
 * @date 2018年6月15日
 * @version 0.0.1
 */
@Slf4j
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnBean(AbstractSecurityConfig.class)
@EnableConfigurationProperties({ SecurityProperties.class, SocialProperties.class })
@Import({ SecurityRedisAutoConfiguration.class, SecurityAuthorizeProviderAutoConfiguration.class,
		SecurityCodeAutoConfiguration.class, SecurityHandlerAutoConfiguration.class, WebSecurityAutoConfiguration.class,
		HttpSecurityAutoConfiguration.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class SecurityCoreAutoConfiguration {

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
	@Bean("userDetailsService")
	@ConditionalOnMissingBean(name="userDetailsService")
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return new CustomeUserDetailsServiceImpl(passwordEncoder);
	}

	/**
	 * 将密码加密类注入到spring security中<br/>
	 * 
	 * <pre>
	 * 此配置会被AbstractSecurityConfig收集，通过public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception 注入到spring security中
	 * </pre>
	 * 
	 * @return
	 */
	@Bean("authenticationProvider")
	@ConditionalOnMissingBean(name = "authenticationProvider")
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		authenticationProvider.setHideUserNotFoundExceptions(false);
		return authenticationProvider;
	}

	/**
	 * 错误提示信息国际化
	 * 
	 * @return
	 */
	@Bean("messageSource")
	@ConditionalOnMissingBean(name = "messageSource")
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath*:messages_zh_CN", "classpath:messages_zh_CN",
				"classpath*:messages_zh_CN.properties", "messages_zh_CN.properties");
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
	 * 注入一个资源管理器
	 * 
	 * @param securityProperties
	 * @param socialProperties
	 * @return
	 */
	@Bean
	public PropertyResource propertyResource(SecurityProperties securityProperties, SocialProperties socialProperties) {
		SimplePropertyResource propertyResource = new SimplePropertyResource();
		propertyResource.setSecurityProperties(securityProperties);
		propertyResource.setSocialProperties(socialProperties);
		return propertyResource;
	}

	/**
	 * 注入一个安全管理器
	 * 
	 * @param authorizeConfigProviders
	 * @param interceptors
	 * @param webSecurityProviders
	 * @param propertyResource
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public SecurityContextManager securityContextManager(List<AuthorizeProvider> authorizeConfigProviders,
			List<HttpSecurityInterceptor> interceptors, List<WebSecurityProvider> webSecurityProviders,
			PropertyResource propertyResource) {
		SimpleSecurityContextManager securityContextManager = new SimpleSecurityContextManager();
		securityContextManager.setInterceptors(interceptors);
		securityContextManager.setAuthorizeConfigProviders(authorizeConfigProviders);
		securityContextManager.setWebSecurityProviders(webSecurityProviders);
		securityContextManager.setPropertyResource(propertyResource);
		return securityContextManager;
	}

	/**
	 * 注入一个默认的协助处理器
	 * 
	 * @param objectMapper
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public HandlerProcessor handlerProcessor() {
		SimpleHandlerProcessor handlerProcessor = new SimpleHandlerProcessor();
		return handlerProcessor;
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <Security相关配置> 相关的配置");
	}

}
