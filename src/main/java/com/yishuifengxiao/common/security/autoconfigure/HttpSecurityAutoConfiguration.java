package com.yishuifengxiao.common.security.autoconfigure;

import javax.servlet.ServletException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.yishuifengxiao.common.code.CodeProcessor;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.authentcation.SmsUserDetailsService;
import com.yishuifengxiao.common.security.extractor.SecurityExtractor;
import com.yishuifengxiao.common.security.extractor.SecurityTokenExtractor;
import com.yishuifengxiao.common.security.extractor.impl.SimpleSecurityTokenExtractor;
import com.yishuifengxiao.common.security.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.filter.impl.TokenValidateFilter;
import com.yishuifengxiao.common.security.filter.impl.UsernamePasswordAuthFilter;
import com.yishuifengxiao.common.security.filter.impl.ValidateCodeFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.AuthorizeResourceInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.SmsLoginInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.builder.SimpleTokenBuilder;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.security.token.holder.impl.InMemoryTokenHolder;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.impl.FirewallWebSecurityProvider;
import com.yishuifengxiao.common.security.websecurity.impl.IgnoreResourceProvider;

/**
 * <p>
 * 配置系统中的资源授权拦截器<code>HttpSecurityInterceptor</code>
 * </p>
 * 
 * 功能如下： 1 配置需要拦截哪些资源 2 配置异常处理方式
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class, AbstractSecurityConfig.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class HttpSecurityAutoConfiguration {

	/**
	 * 注入一个基于内存的token存取工具
	 * 
	 * @return token存取工具
	 */
	@Bean
	@ConditionalOnMissingBean(name = { "redisTemplate" }, value = { TokenHolder.class })
	public TokenHolder tokenHolder() {
		return new InMemoryTokenHolder();
	}

	/**
	 * 注入一个TokenBuilder
	 * 
	 * @param tokenHolder token存取工具
	 * @return TokenBuilder实例
	 */
	@Bean
	@ConditionalOnMissingBean({ TokenBuilder.class })
	public TokenBuilder tokenBuilder(TokenHolder tokenHolder) {
		SimpleTokenBuilder simpleTokenBuilder = new SimpleTokenBuilder();
		simpleTokenBuilder.setTokenHolder(tokenHolder);
		return simpleTokenBuilder;
	}

	/**
	 * 配置需要拦截哪些资源
	 * 
	 * @param propertyResource 资源管理器
	 * @return 资源授权拦截器
	 */
	@Bean("authorizeResourceInterceptor")
	@ConditionalOnMissingBean(name = { "authorizeResourceInterceptor" })
	public HttpSecurityInterceptor authorizeResourceInterceptor(PropertyResource propertyResource) {
		AuthorizeResourceInterceptor authorizeResourceProvider = new AuthorizeResourceInterceptor();
		authorizeResourceProvider.setPropertyResource(propertyResource);
		return authorizeResourceProvider;
	}

	@Bean("usernamePasswordAuthFilter")
	@ConditionalOnMissingBean(name = { "usernamePasswordAuthFilter" })
	public SecurityRequestFilter usernamePasswordAuthFilter(HandlerProcessor handlerProcessor,
			SecurityHelper securityHelper, PropertyResource propertyResource, SecurityExtractor securityExtractor) {
		UsernamePasswordAuthFilter usernamePasswordAuthFilter = new UsernamePasswordAuthFilter(handlerProcessor,
				securityHelper, propertyResource, securityExtractor);
		return usernamePasswordAuthFilter;
	}

	@Bean
	@ConditionalOnMissingBean({ SecurityTokenExtractor.class })
	public SecurityTokenExtractor securityTokenExtractor() {
		return new SimpleSecurityTokenExtractor();
	}

	@Bean("securityTokenValidateFilter")
	@ConditionalOnMissingBean(name = { "securityTokenValidateFilter" })
	public SecurityRequestFilter securityTokenValidateFilter(PropertyResource propertyResource,
			HandlerProcessor handlerProcessor, SecurityTokenExtractor securityTokenExtractor,
			SecurityHelper securityHelper) throws ServletException {

		TokenValidateFilter tokenValidateFilter = new TokenValidateFilter(propertyResource, handlerProcessor,
				securityTokenExtractor, securityHelper);
		tokenValidateFilter.afterPropertiesSet();
		return tokenValidateFilter;
	}

	/**
	 * 注入一个验证码过滤器
	 * 
	 * @param codeProcessor      验证码处理器
	 * @param securityProperties 安全属性配置
	 * @param handlerProcessor   协助处理器
	 * @return 验证码过滤器
	 */
	@Bean("validateCodeFilter")
	@ConditionalOnMissingBean(name = "validateCodeFilter")
	@ConditionalOnBean({ CodeRepository.class })
	public SecurityRequestFilter validateCodeFilter(CodeProcessor codeProcessor, SecurityProperties securityProperties,
			HandlerProcessor handlerProcessor) {
		ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
		validateCodeFilter.setCodeProcessor(codeProcessor);
		validateCodeFilter.setSecurityProperties(securityProperties);
		validateCodeFilter.setHandlerProcessor(handlerProcessor);
		return validateCodeFilter;
	}

	/**
	 * 注入短信登录配置
	 * <p>
	 * 配置短信验证码登陆功能
	 * </p>
	 * 要想使短信验证码功能生效，需要配置： 1
	 * 先配置一个短信登陆地址属性(<code>yishuifengxiao.security.code.sms-login-url</code>), 2
	 * 再配置一个名为 smsUserDetailsService 的 <code>UserDetailsService</code> 实例
	 * 
	 * @param authenticationFailureHandler 认证失败处理器
	 * @param authenticationSuccessHandler 认证成功处理器
	 * @param smsUserDetailsService        短信登陆逻辑
	 * @param securityProperties           安全属性配置
	 * @return 资源授权拦截器实例
	 */
	@Bean("smsLoginInterceptor")
	@ConditionalOnProperty(prefix = "yishuifengxiao.security.code", name = "sms-login-url")
	@ConditionalOnMissingBean(name = "smsLoginInterceptor")
	@ConditionalOnBean({ SmsUserDetailsService.class })
	public HttpSecurityInterceptor smsLoginInterceptor(AuthenticationSuccessHandler authenticationFailureHandler,
			AuthenticationFailureHandler authenticationSuccessHandler, SmsUserDetailsService smsUserDetailsService,
			SecurityProperties securityProperties) {

		return new SmsLoginInterceptor(authenticationFailureHandler, authenticationSuccessHandler,
				smsUserDetailsService, securityProperties.getCode().getSmsLoginUrl());
	}

	/**
	 * <p>
	 * 忽视资源授权器
	 * </p>
	 * 
	 * @param securityProperties 安全属性配置
	 * @return web安全授权器实例
	 */
	@Bean(name = "ignoreResourceProvider")
	@ConditionalOnMissingBean(name = { "ignoreResourceProvider" })
	public WebSecurityProvider ignoreResourceProvider(SecurityProperties securityProperties) {
		IgnoreResourceProvider ignoreResourceProvider = new IgnoreResourceProvider();
		return ignoreResourceProvider;
	}

	/**
	 * 默认实现的HttpFirewall，主要是解决路径里包含 // 路径报错的问题
	 * 
	 * @return web安全授权器实例
	 */
	@Bean("firewallWebSecurityProvider")
	@ConditionalOnMissingBean(name = { "firewallWebSecurityProvider" })
	public WebSecurityProvider firewallWebSecurityProvider() {
		return new FirewallWebSecurityProvider();
	}

}
