package com.yishuifengxiao.common.social.support;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2AccessTokenResponseClient的组合类，使用了Composite Pattern（组合模式）
 * 除了同时支持GOOGLE，OKTA，GITHUB，FACEBOOK之外，可能还需要同时支持QQ、微信等多种认证服务
 * 根据registrationId选择相应的OAuth2AccessTokenResponseClient
 */
public class CompositeOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private Map<String, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> clients = new HashMap<>();

    /**
     * spring-security-oauth2-client默认的OAuth2AccessTokenResponseClient是DefaultAuthorizationCodeTokenResponseClient
     * 将其预置到组合类CompositeOAuth2AccessTokenResponseClient中，从而默认支持GOOGLE，OKTA，GITHUB，FACEBOOK
     */
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> defaultClient = new DefaultAuthorizationCodeTokenResponseClient();


    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = authorizationGrantRequest.getClientRegistration();
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client = clients.get(clientRegistration.getRegistrationId());
        if (client == null) {
            client = defaultClient;
        }
        return client.getTokenResponse(authorizationGrantRequest);
    }

    public CompositeOAuth2AccessTokenResponseClient add(String registrationId, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client) {
        if (StringUtils.isNotBlank(registrationId) && null != client) {
            clients.put(registrationId, client);
        }
        return this;
    }

}