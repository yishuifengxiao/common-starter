package com.yishuifengxiao.common.oauth2.configurer;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Configures OAuth 2.0 Client Authentication.
 * OAuth2ClientAuthenticationFilter is configured with the following defaults:
 * <p>
 * AuthenticationConverter — A composed of , , , and .DelegatingAuthenticationConverterJwtClientAssertionAuthenticationConverterClientSecretBasicAuthenticationConverterClientSecretPostAuthenticationConverterPublicClientAuthenticationConverter
 * <p>
 * AuthenticationManager — An composed of , , and .AuthenticationManagerJwtClientAssertionAuthenticationProviderClientSecretAuthenticationProviderPublicClientAuthenticationProvider
 * <p>
 * AuthenticationSuccessHandler — An internal implementation that associates the “authenticated” (current ) to the .OAuth2ClientAuthenticationTokenAuthenticationSecurityContext
 * <p>
 * AuthenticationFailureHandler — An internal implementation that uses the associated with the to return the OAuth2 error response.OAuth2ErrorOAuth2AuthenticationException
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleOAuth2ClientAuthenticationConfigurer implements Customizer<OAuth2ClientAuthenticationConfigurer>{

    private AuthenticationSuccessHandler authenticationSuccessHandler;

    private AuthenticationFailureHandler errorResponseHandler;


    public SimpleOAuth2ClientAuthenticationConfigurer(AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler errorResponseHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.errorResponseHandler = errorResponseHandler;
    }


    @Override
    public void customize(OAuth2ClientAuthenticationConfigurer oAuth2ClientAuthenticationConfigurer) {
        oAuth2ClientAuthenticationConfigurer.authenticationSuccessHandler(this.authenticationSuccessHandler)
                .errorResponseHandler(this.errorResponseHandler);
    }
}
