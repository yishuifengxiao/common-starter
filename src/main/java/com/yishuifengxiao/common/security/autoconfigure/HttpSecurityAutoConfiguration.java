package com.yishuifengxiao.common.security.autoconfigure;

import javax.servlet.Filter;
import javax.servlet.ServletException;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.filter.UserAuthServiceFilter;
import com.yishuifengxiao.common.security.filter.UsernamePasswordAuthFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.AuthorizeResourceInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.UserAuthServiceInterceptor;
import com.yishuifengxiao.common.security.httpsecurity.impl.UsernameExtisAuthInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.token.builder.SimpleTokenBuilder;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.security.token.holder.impl.InMemoryTokenHolder;

/**
 * 配置系统中的资源授权拦截器<code>HttpSecurityInterceptor</code><br/>
 * <br/>
 * 功能如下：<br/>
 * 1 配置需要拦截哪些资源<br/>
 * 2 配置异常处理方式<br/>
 * 
 * @author yishui
 * @version 0.0.1
 * @date 2018年6月15日
 */
@Configuration
@ConditionalOnBean(AbstractSecurityConfig.class)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class, AbstractSecurityConfig.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class HttpSecurityAutoConfiguration {

	/**
	 * 配置需要拦截哪些资源
	 * 
	 * @param securityProperties
	 * @param socialProperties
	 * @return
	 */
	@Bean("authorizeResourceInterceptor")
	@ConditionalOnMissingBean(name = { "authorizeResourceInterceptor" })
	public HttpSecurityInterceptor authorizeResourceInterceptor(PropertyResource propertyResource) {
		AuthorizeResourceInterceptor authorizeResourceProvider = new AuthorizeResourceInterceptor();
		authorizeResourceProvider.setPropertyResource(propertyResource);
		return authorizeResourceProvider;
	}

	/**
	 * 提前校验一下用户名是否已经存在
	 * 
	 * @param authenticationFailureHandler
	 * @param userDetailsService
	 * @param securityProperties
	 * @return
	 */
	@Bean("usernamePasswordAuthFilter")
	@ConditionalOnMissingBean(name = { "usernamePasswordAuthFilter" })
	public Filter usernamePasswordAuthFilter(HandlerProcessor handlerProcessor,
			@Qualifier("userDetailsService")  UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder, SecurityProperties securityProperties) {
		UsernamePasswordAuthFilter usernamePasswordAuthFilter = new UsernamePasswordAuthFilter();
		usernamePasswordAuthFilter.setHandlerProcessor(handlerProcessor);
		usernamePasswordAuthFilter.setSecurityProperties(securityProperties);
		usernamePasswordAuthFilter.setUserDetailsService(userDetailsService);
		usernamePasswordAuthFilter.setPasswordEncoder(passwordEncoder);
		return usernamePasswordAuthFilter;
	}

	/**
	 * 注入用户名校验过滤器
	 * 
	 * @param usernameExtisAuthFilter
	 * @return
	 */
	@Bean("usernameExtisAuthInterceptor")
	@ConditionalOnMissingBean(name = { "usernameExtisAuthInterceptor" })
	public HttpSecurityInterceptor usernameExtisAuthInterceptor(
			@Qualifier("usernamePasswordAuthFilter") Filter usernamePasswordAuthFilter) {
		UsernameExtisAuthInterceptor usernameExtisAuthInterceptor = new UsernameExtisAuthInterceptor();
		usernameExtisAuthInterceptor.setUsernamePasswordAuthFilter(usernamePasswordAuthFilter);
		return usernameExtisAuthInterceptor;
	}

	@Bean
	@ConditionalOnMissingBean(name = { "redisTemplate", "tokenHolder" })
	public TokenHolder tokenHolder() {
		return new InMemoryTokenHolder();
	}

	/**
	 * 注入一个TokenBuilder
	 * 
	 * @param tokenHolder
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenBuilder tokenBuilder(TokenHolder tokenHolder) {
		SimpleTokenBuilder simpleTokenBuilder = new SimpleTokenBuilder();
		simpleTokenBuilder.setTokenHolder(tokenHolder);
		return simpleTokenBuilder;
	}

	/**
	 * 注入一个UserAuthServiceFilter
	 * 
	 * @param propertyResource
	 * @param handlerProcessor
	 * @param tokenBuilder
	 * @param userDetailsService
	 * @return
	 * @throws ServletException
	 */
	@Bean("userAuthServiceFilter")
	@ConditionalOnMissingBean(name = { "userAuthServiceFilter" })
	public Filter userAuthServiceFilter(PropertyResource propertyResource, HandlerProcessor handlerProcessor,
			TokenBuilder tokenBuilder,@Qualifier("userDetailsService")  UserDetailsService userDetailsService) throws ServletException {

		UserAuthServiceFilter userAuthServiceFilter = new UserAuthServiceFilter();
		userAuthServiceFilter.setPropertyResource(propertyResource);
		userAuthServiceFilter.setHandlerProcessor(handlerProcessor);
		userAuthServiceFilter.setTokenBuilder(tokenBuilder);
		userAuthServiceFilter.setUserDetailsService(userDetailsService);
		userAuthServiceFilter.afterPropertiesSet();
		return userAuthServiceFilter;
	}

	/**
	 * 配置UserAuthServiceFilter
	 * 
	 * @param userAuthServiceFilter
	 * @return
	 */
	@Bean("userAuthServiceInterceptor")
	@ConditionalOnBean(name = "userAuthServiceFilter")
	@ConditionalOnMissingBean(name = { "userAuthServiceInterceptor" })
	public HttpSecurityInterceptor userAuthServiceInterceptor(
			@Qualifier("userAuthServiceFilter") Filter userAuthServiceFilter) {
		UserAuthServiceInterceptor userAuthServiceInterceptor = new UserAuthServiceInterceptor();
		userAuthServiceInterceptor.setUserAuthServiceFilter(userAuthServiceFilter);
		return userAuthServiceInterceptor;
	}

}
