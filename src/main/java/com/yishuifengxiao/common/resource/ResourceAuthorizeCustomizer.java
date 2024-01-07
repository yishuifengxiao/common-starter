package com.yishuifengxiao.common.resource;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

/**
 * 资源服务器授权提供器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ResourceAuthorizeCustomizer implements AuthorizeCustomizer {


    private BearerTokenResolver customBearerTokenResolver;

    private OpaqueTokenIntrospector customOpaqueTokenIntrospector;


    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        //@formatter:off


		http.oauth2ResourceServer()
				.authenticationEntryPoint(authenticationPoint)
				.accessDeniedHandler(authenticationPoint)
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


}
