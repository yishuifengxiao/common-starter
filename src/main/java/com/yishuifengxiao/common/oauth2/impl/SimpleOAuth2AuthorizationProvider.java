package com.yishuifengxiao.common.oauth2.impl;

import com.yishuifengxiao.common.oauth2.OAuth2AuthorizationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2ClientAuthenticationConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleOAuth2AuthorizationProvider implements OAuth2AuthorizationProvider {

    private RegisteredClientRepository registeredClientRepository;

    private AuthorizationServerSettings authorizationServerSettings;

    private Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthentication;

    private OAuth2AuthorizationService authorizationService;

    private OAuth2AuthorizationConsentService authorizationConsentService;


    public SimpleOAuth2AuthorizationProvider(RegisteredClientRepository registeredClientRepository,
                                             AuthorizationServerSettings authorizationServerSettings,
                                             Customizer<OAuth2ClientAuthenticationConfigurer> clientAuthentication,
                                             OAuth2AuthorizationService authorizationService,
                                             OAuth2AuthorizationConsentService authorizationConsentService) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationServerSettings = authorizationServerSettings;
        this.clientAuthentication = clientAuthentication;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
    }

    @Override
    public void apply(OAuth2AuthorizationServerConfigurer authorizationServerConfigurer) {
        authorizationServerConfigurer
                // (REQUIRED) for managing new and existing clients.
                .registeredClientRepository(registeredClientRepository)
                // (REQUIRED) for customizing configuration settings for the OAuth2 authorization server. setting
                // endpoint url
                .authorizationServerSettings(authorizationServerSettings)
                //用于管理新授权和现有授权的OAuth2AuthorizationService。
                .authorizationService(authorizationService)
                //用于管理新的和现有的授权同意的OAuth2AuthorizationConsentService。
                .authorizationConsentService(authorizationConsentService)
                //Configures OAuth 2.0 Client Authentication
                .clientAuthentication(clientAuthentication);

    }
}
