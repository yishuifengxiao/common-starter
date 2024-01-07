package com.yishuifengxiao.common.oauth2server.customizer;

import com.yishuifengxiao.common.security.SecurityPropertyResource;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityEnhanceCustomizer;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class OAuth2HttpSecurityEnhanceCustomizer implements HttpSecurityEnhanceCustomizer {
    private AuthenticationPoint authenticationPoint;

    @Override
    public void apply(SecurityPropertyResource securityPropertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {

        http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {

            httpSecurityOAuth2ResourceServerConfigurer
                    .accessDeniedHandler(this.authenticationPoint)
                    .authenticationEntryPoint(this.authenticationPoint)
                    .jwt(Customizer.withDefaults())
            ;

        });

    }

    @Override
    public int order() {
        return 1500;
    }

    public OAuth2HttpSecurityEnhanceCustomizer(AuthenticationPoint authenticationPoint) {
        this.authenticationPoint = authenticationPoint;
    }
}
