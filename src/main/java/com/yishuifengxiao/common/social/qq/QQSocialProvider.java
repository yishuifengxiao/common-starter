package com.yishuifengxiao.common.social.qq;

import com.yishuifengxiao.common.social.SocialProvider;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * QQ 登录流程配置.
 * 相对于标准的OAuth2授权码模式，QQ提供的API在交互上较为混乱，其响应类型为text/html，响应内容则同时存在普通文本、JSONP、JSON字符串等多种类型。
 * 另外，QQ提供的API还需要先获取OpenId，再使用OpenId结合appId与access_token的方式来获取用户信息，而不是直接使用access_token，这些都是我们需要自定义实现的重点内容。
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class QQSocialProvider implements SocialProvider {
    private final static String clientRegistrationId = "qq";

    @Override
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        return new QQOAuth2AccessTokenResponseClient();
    }

    @Override
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> userService() {
        return new QQOAuth2UserService();
    }

    @Override
    public String clientRegistrationId() {
        return clientRegistrationId;
    }

    @Override
    public Class<? extends OAuth2User> customUserType() {
        return QQUserInfo.class;
    }
}
