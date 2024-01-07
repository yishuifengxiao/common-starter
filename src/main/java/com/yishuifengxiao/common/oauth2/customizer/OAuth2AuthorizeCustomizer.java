package com.yishuifengxiao.common.oauth2.customizer;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeCustomizer;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.SecurityPropertyResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class OAuth2AuthorizeCustomizer implements AuthorizeCustomizer {
    private AuthenticationPoint authenticationPoint;

    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        OAuth2ResourceServerConfigurer<HttpSecurity> oauth2ResourceServer = http.oauth2ResourceServer();
        oauth2ResourceServer.jwt();
        oauth2ResourceServer.accessDeniedHandler(this.authenticationPoint).authenticationEntryPoint(this.authenticationPoint);

    }

    @Override
    public int order() {
        return 1500;
    }

    public OAuth2AuthorizeCustomizer(AuthenticationPoint authenticationPoint) {
        this.authenticationPoint = authenticationPoint;
    }
}
