package com.yishuifengxiao.common.resource;

import com.yishuifengxiao.common.resource.introspection.CustomOpaqueTokenIntrospector;
import com.yishuifengxiao.common.resource.resolver.CustomBearerTokenResolver;
import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * 资源服务器自动配置
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */

@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@EnableConfigurationProperties(ResourceProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "yishuifengxiao.security.resourceserver", name = {"token-check-url"}, matchIfMissing = false)
public class SecurityResourceEnhanceAutoConfiguration {


    @Bean("customBearerTokenResolver")
    @ConditionalOnMissingBean(name = {"customBearerTokenResolver"})
    public BearerTokenResolver customBearerTokenResolver() {
        return new CustomBearerTokenResolver();
    }

    @Bean("customOpaqueTokenIntrospector")
    @ConditionalOnMissingBean(name = {"customOpaqueTokenIntrospector"})
    public OpaqueTokenIntrospector customOpaqueTokenIntrospector(ResourceProperties resourceProperties) {
        return new CustomOpaqueTokenIntrospector(resourceProperties.getTokenCheckUrl());
    }

    @Bean("resourceAuthorizeProvider")
    @ConditionalOnMissingBean(name = {"resourceAuthorizeProvider"})
    public AuthorizeCustomizer resourceAuthorizeProvider(@Qualifier("customBearerTokenResolver") BearerTokenResolver customBearerTokenResolver, @Qualifier("customOpaqueTokenIntrospector") OpaqueTokenIntrospector customOpaqueTokenIntrospector) {
        ResourceAuthorizeCustomizer resourceAuthorizeProvider = new ResourceAuthorizeCustomizer();
        resourceAuthorizeProvider.setCustomBearerTokenResolver(customBearerTokenResolver);
        resourceAuthorizeProvider.setCustomOpaqueTokenIntrospector(customOpaqueTokenIntrospector);
        return resourceAuthorizeProvider;
    }
}
