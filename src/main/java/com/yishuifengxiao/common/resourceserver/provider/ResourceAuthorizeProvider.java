package com.yishuifengxiao.common.resourceserver.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * 资源服务器授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceAuthorizeProvider implements AuthorizeProvider {

    private AuthenticationEntryPoint resourceAuthenticationEntryPoint;

    private BearerTokenResolver customBearerTokenResolver;

    private OpaqueTokenIntrospector customOpaqueTokenIntrospector;

    private AccessDeniedHandler accessDeniedHandler;

    @Override
    public void apply(PropertyResource propertyResource, HttpSecurity http) throws Exception {
        //@formatter:off


		http.oauth2ResourceServer()
				.authenticationEntryPoint(resourceAuthenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler)
				.bearerTokenResolver(customBearerTokenResolver)
				.opaqueToken()
				.introspector(customOpaqueTokenIntrospector)
				;
		//@formatter:on  
    }

    @Override
    public int order() {
        return 2000;
    }

    public AuthenticationEntryPoint getResourceAuthenticationEntryPoint() {
        return resourceAuthenticationEntryPoint;
    }

    public void setResourceAuthenticationEntryPoint(AuthenticationEntryPoint resourceAuthenticationEntryPoint) {
        this.resourceAuthenticationEntryPoint = resourceAuthenticationEntryPoint;
    }

    public BearerTokenResolver getCustomBearerTokenResolver() {
        return customBearerTokenResolver;
    }

    public void setCustomBearerTokenResolver(BearerTokenResolver customBearerTokenResolver) {
        this.customBearerTokenResolver = customBearerTokenResolver;
    }

    public OpaqueTokenIntrospector getCustomOpaqueTokenIntrospector() {
        return customOpaqueTokenIntrospector;
    }

    public void setCustomOpaqueTokenIntrospector(OpaqueTokenIntrospector customOpaqueTokenIntrospector) {
        this.customOpaqueTokenIntrospector = customOpaqueTokenIntrospector;
    }

    public AccessDeniedHandler getAccessDeniedHandler() {
        return accessDeniedHandler;
    }

    public void setAccessDeniedHandler(AccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

}
