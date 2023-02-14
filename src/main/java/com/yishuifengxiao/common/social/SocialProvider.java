package com.yishuifengxiao.common.social;

import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SocialProvider {

    /**
     * Sets the client used for requesting the access token credential from the Token Endpoint.
     *
     * @return the client used for requesting the access token credential from the Token Endpoin
     */
    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient();

    /**
     * Sets the OAuth 2.0 service used for obtaining the user attributes of the End-User from the UserInfo Endpoint.
     *
     * @return the OAuth 2.0 service used for obtaining the user attributes of the End-User from the UserInfo Endpoint
     */
    OAuth2UserService<OAuth2UserRequest, OAuth2User> userService();

    /**
     * the client registration identifier
     *
     * @return the client registration identifier
     */
    String clientRegistrationId();

    /**
     * a custom OAuth2User type
     *
     * @return a custom OAuth2User type
     */
    Class<? extends org.springframework.security.oauth2.core.user.OAuth2User> customUserType();
}
