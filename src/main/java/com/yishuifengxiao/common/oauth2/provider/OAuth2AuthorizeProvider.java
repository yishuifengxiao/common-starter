/**
 *
 */
package com.yishuifengxiao.common.oauth2.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;


/**
 * @author yishui
 *
 */
public class OAuth2AuthorizeProvider implements AuthorizeProvider {

    private RegisteredClientRepository registeredClientRepository;

    private ProviderSettings providerSettings;

    private OAuth2AuthorizationService authorizationService;

    private OAuth2AuthorizationConsentService authorizationConsentService;


    @SuppressWarnings({"rawtypes", "unused"})
    @Override
    public void apply(PropertyResource propertyResource, SecurityHandler securityHandler, HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        http.apply(authorizationServerConfigurer);
        // @formatter:off
         OAuth2AuthorizationServerConfigurer configurer = authorizationServerConfigurer
                // (REQUIRED) for managing new and existing clients.
                .registeredClientRepository(registeredClientRepository)
                 // (REQUIRED) for customizing configuration settings for the OAuth2 authorization server. setting endpoint url
                 .providerSettings(providerSettings)
                //用于管理新授权和现有授权的OAuth2AuthorizationService。
                .authorizationService(authorizationService)
                //用于管理新的和现有的授权同意的OAuth2AuthorizationConsentService。
                .authorizationConsentService(authorizationConsentService)
                //Configures OAuth 2.0 Client Authentication
                .clientAuthentication(clientAuthenticationCustomizer->clientAuthenticationCustomizer.authenticationSuccessHandler(securityHandler).errorResponseHandler(securityHandler))
                 .tokenEndpoint(tokenEndpointCustomizer->tokenEndpointCustomizer.accessTokenResponseHandler(securityHandler).errorResponseHandler(securityHandler));

        http.authorizeRequests().mvcMatchers(providerSettings.getTokenEndpoint()).permitAll();
        http.authorizeRequests().antMatchers(providerSettings.getTokenEndpoint()).permitAll();
    }



    @Override
    public int order() {
        return 0;
    }

    public OAuth2AuthorizeProvider(RegisteredClientRepository registeredClientRepository,
                                   ProviderSettings providerSettings,
                                   OAuth2AuthorizationService authorizationService,
                                   OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.providerSettings = providerSettings;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
    }    // @formatter:on
}
