/**
 *
 */
package com.yishuifengxiao.common.oauth2.provider;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.security.support.PropertyResource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * @author yishui
 */
public class OAuth2AuthorizeProvider implements AuthorizeProvider {

    private RegisteredClientRepository registeredClientRepository;

    private ProviderSettings providerSettings;

    private Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthentication;

    private OAuth2AuthorizationService authorizationService;

    private OAuth2AuthorizationConsentService authorizationConsentService;


    @SuppressWarnings("unchecked")
    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {


        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer();
        authorizationServerConfigurer
                .oidc(Customizer.withDefaults());    // Enable OpenID Connect 1.0

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();


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
                .clientAuthentication(clientAuthentication);
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                );

        http.apply(configurer);


    }


    // @formatter:on
    @Override
    public int order() {
        return 10000;
    }

    public OAuth2AuthorizeProvider(RegisteredClientRepository registeredClientRepository,
                                   ProviderSettings providerSettings,
                                   Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthentication,
                                   OAuth2AuthorizationService authorizationService,
                                   OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.providerSettings = providerSettings;
        this.clientAuthentication = clientAuthentication;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
    }
}
