package com.yishuifengxiao.common.resourceserver;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.yishuifengxiao.common.resourceserver.endpoint.ResourceAuthenticationEntryPoint;
import com.yishuifengxiao.common.resourceserver.introspection.CustomOpaqueTokenIntrospector;
import com.yishuifengxiao.common.resourceserver.provider.ResourceAuthorizeProvider;
import com.yishuifengxiao.common.resourceserver.resolver.CustomBearerTokenResolver;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;

/**
 * @author yishui
 * @version 1.0.0
 * @date 2019-10-29
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(AbstractSecurityConfig.class)
@AutoConfigureBefore({ SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
@EnableConfigurationProperties(ResourceProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "yishuifengxiao.security.resourceserver", name = {
		"enable" }, havingValue = "true", matchIfMissing = false)
public class SecurityResourceAutoConfiguration {

	@Bean("resourceAuthenticationEntryPoint")
	@ConditionalOnMissingBean(name = { "resourceAuthenticationEntryPoint" })
	public AuthenticationEntryPoint resourceAuthenticationEntryPoint(HandlerProcessor handlerProcessor) {
		ResourceAuthenticationEntryPoint resourceAuthenticationEntryPoint = new ResourceAuthenticationEntryPoint();
		resourceAuthenticationEntryPoint.setHandlerProcessor(handlerProcessor);
		return resourceAuthenticationEntryPoint;
	}

	@Bean("customBearerTokenResolver")
	@ConditionalOnMissingBean(name = { "customBearerTokenResolver" })
	public CustomBearerTokenResolver customBearerTokenResolver() {
		return new CustomBearerTokenResolver();
	}

	@Bean("customOpaqueTokenIntrospector")
	@ConditionalOnMissingBean(name = { "customOpaqueTokenIntrospector" })
	public OpaqueTokenIntrospector customOpaqueTokenIntrospector(ResourceProperties resourceProperties) {
		return new CustomOpaqueTokenIntrospector(resourceProperties.getTokenCheckUrl());
	}

	@Bean("resourceAuthorizeProvider")
	@ConditionalOnMissingBean(name = { "resourceAuthorizeProvider" })
	public AuthorizeProvider resourceAuthorizeProvider(
			@Qualifier("resourceAuthenticationEntryPoint") AuthenticationEntryPoint resourceAuthenticationEntryPoint,
			@Qualifier("customBearerTokenResolver") CustomBearerTokenResolver customBearerTokenResolver,
			@Qualifier("customOpaqueTokenIntrospector") OpaqueTokenIntrospector customOpaqueTokenIntrospector,
			@Qualifier("accessDeniedHandler") AccessDeniedHandler accessDeniedHandler) {
		ResourceAuthorizeProvider resourceAuthorizeProvider = new ResourceAuthorizeProvider();
		resourceAuthorizeProvider.setCustomBearerTokenResolver(customBearerTokenResolver);
		resourceAuthorizeProvider.setCustomOpaqueTokenIntrospector(customOpaqueTokenIntrospector);
		resourceAuthorizeProvider.setResourceAuthenticationEntryPoint(resourceAuthenticationEntryPoint);
		resourceAuthorizeProvider.setAccessDeniedHandler(accessDeniedHandler);
		return resourceAuthorizeProvider;
	}
}
