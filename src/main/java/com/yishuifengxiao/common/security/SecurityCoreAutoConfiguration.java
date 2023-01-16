package com.yishuifengxiao.common.security;

import java.util.List;
import java.util.Locale;

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
import com.yishuifengxiao.common.security.autoconfigure.SecurityAuthorizeProviderAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityHandlerAutoConfiguration;
import com.yishuifengxiao.common.security.autoconfigure.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.security.encoder.impl.SimpleBasePasswordEncoder;
import com.yishuifengxiao.common.security.extractor.SecurityExtractor;
import com.yishuifengxiao.common.security.extractor.impl.SimpleSecurityExtractor;
import com.yishuifengxiao.common.security.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.processor.impl.SimpleHandlerProcessor;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.remerberme.InMemoryTokenRepositoryImpl;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.resource.SimplePropertyResource;
import com.yishuifengxiao.common.security.service.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.session.SessionInformationExpiredStrategyImpl;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.support.SimpleSecurityHelper;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.utils.TokenUtil;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;
import com.yishuifengxiao.common.social.SocialProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * spring security扩展支持自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class })
@ConditionalOnBean(AbstractSecurityConfig.class)
@EnableConfigurationProperties({ SecurityProperties.class, SocialProperties.class })
@Import({ SecurityRedisAutoConfiguration.class, SecurityAuthorizeProviderAutoConfiguration.class,
		SecurityHandlerAutoConfiguration.class, HttpSecurityAutoConfiguration.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class SecurityCoreAutoConfiguration {

	/**
	 * 注入自定义密码加密类
	 * 
	 * @param propertyResource 资源管理器
	 * @return 加密器
	 */
	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder(PropertyResource propertyResource) {
		return new SimpleBasePasswordEncoder(propertyResource);
	}

	/**
	 * <p>
	 * 注入用户查找配置类
	 * </p>
	 * 在系统没有注入UserDetailsService时，注册一个默认的UserDetailsService实例
	 * 
	 * @param passwordEncoder 加密器
	 * @return UserDetailsService
	 */
	@Bean
	@ConditionalOnMissingBean({ UserDetailsService.class })
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		return new CustomeUserDetailsServiceImpl(passwordEncoder);
	}

	/**
	 * <p>
	 * 将密码加密类注入到spring security中
	 * </p>
	 * 
	 * <pre>
	 * 此配置会被AbstractSecurityConfig收集，通过public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception 注入到spring security中
	 * </pre>
	 * 
	 * @param userDetailsService UserDetailsService
	 * @param passwordEncoder    加密器
	 * @return DaoAuthenticationProvider
	 */
	@Bean
	@ConditionalOnMissingBean({ DaoAuthenticationProvider.class })
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
	 * @return ReloadableResourceBundleMessageSource
	 */
	@Bean("messageSource")
	@ConditionalOnMissingBean(name= {"messageSource"})
	public ReloadableResourceBundleMessageSource messageSource() {
	    Locale.setDefault(Locale.CHINA);
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath*:messages_zh_CN", "classpath:messages_zh_CN",
				"classpath*:messages_zh_CN.properties", "messages_zh_CN.properties");
		return messageSource;
	}

	/**
	 * 错误提示国际化
	 * 
	 * @return AcceptHeaderLocaleResolver
	 */
	@Bean
	public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
		return new AcceptHeaderLocaleResolver();
	}

	/**
	 * 记住密码策略【存储内存中在redis数据库中】
	 * 
	 * @return 记住密码策略
	 */
	@Bean
	@ConditionalOnMissingBean(name = { "redisTemplate" }, value = { PersistentTokenRepository.class })
	public PersistentTokenRepository inMemoryTokenRepository() {
		return new InMemoryTokenRepositoryImpl();
	}

	/**
	 * session 失效策略，可以在此方法中记录谁把谁的登陆状态挤掉
	 * 
	 * @return session 失效策略
	 */
	@Bean
	@ConditionalOnMissingBean
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return new SessionInformationExpiredStrategyImpl();
	}

	/**
	 * 注入一个资源管理器
	 * 
	 * @param securityProperties 安全属性配置
	 * @param socialProperties   spring social属性配置
	 * @return 资源管理器
	 */
	@Bean
	public PropertyResource propertyResource(SecurityProperties securityProperties, SocialProperties socialProperties) {
		SimplePropertyResource propertyResource = new SimplePropertyResource();
		propertyResource.setSecurityProperties(securityProperties);
		propertyResource.setSocialProperties(socialProperties);
		return propertyResource;
	}

	/**
	 * 注入一个默认的协助处理器
	 * 
	 * @return 协助处理器
	 */
	@Bean
	@ConditionalOnMissingBean({ HandlerProcessor.class })
	public HandlerProcessor handlerProcessor() {
		SimpleHandlerProcessor handlerProcessor = new SimpleHandlerProcessor();
		return handlerProcessor;
	}

	@Bean
	@ConditionalOnMissingBean({ SecurityExtractor.class })
	public SecurityExtractor securityExtractor(PropertyResource propertyResource) {
		SimpleSecurityExtractor simpleSecurityExtractor = new SimpleSecurityExtractor(propertyResource);
		return simpleSecurityExtractor;
	}

	@Bean
	@ConditionalOnMissingBean({ SecurityHelper.class })
	public SecurityHelper securityHelper(PropertyResource propertyResource, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder, TokenBuilder tokenBuilder) {
		SecurityHelper securityHelper = new SimpleSecurityHelper(propertyResource, userDetailsService, passwordEncoder,
				tokenBuilder);
		return securityHelper;
	}

	@Bean
	@ConditionalOnMissingBean({ TokenUtil.class })
	public TokenUtil tokenUtil(SecurityHelper securityHelper, SecurityExtractor securityExtractor) {
		return new TokenUtil(securityHelper, securityExtractor);
	}

	/**
	 * 注入一个安全管理器
	 * 
	 * @param authorizeConfigProviders 系统中所有的授权提供器实例
	 * @param interceptors             系统中所有的资源授权拦截器实例
	 * @param webSecurityProviders     系统中所有的 web安全授权器实例
	 * @param securityRequestFilters   系统中所有的 web安全授权器实例
	 * @param propertyResource         资源管理器
	 * @return 安全管理器
	 */
	@Bean
	@ConditionalOnMissingBean({ SecurityContextManager.class })
	public SecurityContextManager securityContextManager(List<AuthorizeProvider> authorizeConfigProviders,
			List<HttpSecurityInterceptor> interceptors, List<WebSecurityProvider> webSecurityProviders,
			List<SecurityRequestFilter> securityRequestFilters, PropertyResource propertyResource) {
		SimpleSecurityContextManager securityContextManager = new SimpleSecurityContextManager(authorizeConfigProviders,
				interceptors, webSecurityProviders, propertyResource, securityRequestFilters);
		return securityContextManager;
	}

	@PostConstruct
	public void checkConfig() {

		log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <安全支持> 相关的配置");
	}

}
