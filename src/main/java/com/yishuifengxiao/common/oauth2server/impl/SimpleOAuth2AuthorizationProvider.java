package com.yishuifengxiao.common.oauth2server.impl;

import com.yishuifengxiao.common.oauth2server.OAuth2AuthorizationProvider;
import com.yishuifengxiao.common.oauth2server.Oauth2Properties;
import com.yishuifengxiao.common.oauth2server.token.SimpleOAuth2TokenGenerator;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleOAuth2AuthorizationProvider implements OAuth2AuthorizationProvider {

    private final RegisteredClientRepository registeredClientRepository;

    private final AuthorizationServerSettings authorizationServerSettings;

    private final AuthenticationPoint authenticationPoint;

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2AuthorizationConsentService authorizationConsentService;

    private final Oauth2Properties oauth2Properties;
    private final JwtEncoder jwtEncoder;


    public SimpleOAuth2AuthorizationProvider(RegisteredClientRepository registeredClientRepository,
                                             AuthorizationServerSettings authorizationServerSettings,
                                             AuthenticationPoint authenticationPoint,
                                             OAuth2AuthorizationService authorizationService,
                                             OAuth2AuthorizationConsentService authorizationConsentService,
                                             Oauth2Properties oauth2Properties,
                                             JwtEncoder jwtEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.authorizationServerSettings = authorizationServerSettings;
        this.authenticationPoint = authenticationPoint;
        this.authorizationService = authorizationService;
        this.authorizationConsentService = authorizationConsentService;
        this.oauth2Properties = oauth2Properties;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public void apply(OAuth2AuthorizationServerConfigurer authorizationServerConfigurer) {


        /**
         * 默认情况下，OAuth2 令牌终端节点、OAuth2 令牌侦测终端节点和OAuth2 令牌吊销终端节点需要客户端身份验证。 
         * 支持的客户端身份验证方法包括,,,,和（公共客户端）。
         *  client_secret_basic ,client_secret_post, private_key_jwt , client_secret_jwt, none
         */

        //@formatter:off
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
                //Configures OAuth 2.0 Client Authentication ,OAuth2 客户端身份验证的配置程序。
                .clientAuthentication(clientAuthentication -> clientAuthentication.errorResponseHandler(authenticationPoint))
                // OAuth2 客户端身份验证的配置程序
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint.errorResponseHandler(authenticationPoint))
                //
                .tokenIntrospectionEndpoint(tokenIntrospectionEndpoint -> tokenIntrospectionEndpoint.errorResponseHandler(authenticationPoint))
                //
                .tokenRevocationEndpoint(tokenRevocationEndpoint -> tokenRevocationEndpoint.errorResponseHandler(authenticationPoint))
                //
                .authorizationEndpoint(endpoint -> endpoint
                        //Customizing Authorization Request Validation
                        .authenticationProviders(configureAuthenticationValidator())
                        .errorResponseHandler(authenticationPoint)
                        .consentPage(oauth2Properties.getConsentPage()))

                //
                .oidc(Customizer.withDefaults())

        //
        ;
        //@formatter:on

        authorizationServerConfigurer
                //The OAuth2TokenGenerator for generating tokens supported by the OAuth2 authorization server.
                .tokenGenerator(new SimpleOAuth2TokenGenerator(this.jwtEncoder))
                //	deviceAuthorizationEndpoint(): The configurer for the OAuth2 Device Authorization endpoint.
                .deviceAuthorizationEndpoint(deviceAuthorizationEndpoint -> {
                })
                //	deviceVerificationEndpoint(): The configurer for the OAuth2 Device Verification endpoint.
                .deviceVerificationEndpoint(deviceVerificationEndpoint -> {
                })
                //	authorizationServerMetadataEndpoint(): The configurer for the OAuth2 Authorization Server Metadata endpoint.
                .authorizationServerMetadataEndpoint(authorizationServerMetadataEndpoint -> {
                })
        ;


        /**
         * registeredClientRepository()：用于管理新客户端和现有客户端的已注册客户端存储库（必需）。
         * authorizationService()：用于管理新授权和现有授权的OAuth2AuthorizationService。
         * authorizationConsentService()：OAuth2AuthorizationConsentService用于管理新的和现有的授权同意。
         * authorizationServerSettings()：用于自定义 OAuth2 授权服务器的配置设置的授权服务器设置（必需）。
         * tokenGenerator()：OAuth2TokenGenerator，用于生成 OAuth2 授权服务器支持的令牌。
         * clientAuthentication()：OAuth2 客户端身份验证的配置程序。
         * authorizationEndpoint()：OAuth2 授权端点的配置程序。
         * tokenEndpoint()：OAuth2 令牌端点的配置程序。
         * tokenIntrospectionEndpoint()：OAuth2 令牌侦测端点的配置程序。
         * tokenRevocationEndpoint()：OAuth2 令牌吊销端点的配置程序。
         * authorizationServerMetadataEndpoint()：OAuth2 授权服务器元数据端点的配置程序。
         * providerConfigurationEndpoint()：OpenID Connect 1.0 提供程序配置端点的配置程序。
         * userInfoEndpoint()：OpenID Connect 1.0 UserInfo 端点的配置程序。
         * clientRegistrationEndpoint()：OpenID Connect 1.0 客户端注册端点的配置程序。
         */

    }

    private Consumer<List<AuthenticationProvider>> configureAuthenticationValidator() {
        return (authenticationProviders) ->
                authenticationProviders.forEach((authenticationProvider) -> {
                    if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider) {
                        Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator =
                                // Override default redirect_uri validator
                                new CustomRedirectUriValidator()
                                        // Reuse default scope validator
                                        .andThen(OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_SCOPE_VALIDATOR);

                        ((OAuth2AuthorizationCodeRequestAuthenticationProvider) authenticationProvider)
                                .setAuthenticationValidator(authenticationValidator);
                    }
                });
    }

    static class CustomRedirectUriValidator implements Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> {

        @Override
        public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext authenticationContext) {

            OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                    authenticationContext.getAuthentication();

            RegisteredClient registeredClient = authenticationContext.getRegisteredClient();

            String requestedRedirectUri = authorizationCodeRequestAuthentication.getRedirectUri();


            // Use exact string matching when comparing client redirect URIs against pre-registered URIs
            if (!registeredClient.getRedirectUris().contains(requestedRedirectUri)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
                throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
            }
        }
    }
}
