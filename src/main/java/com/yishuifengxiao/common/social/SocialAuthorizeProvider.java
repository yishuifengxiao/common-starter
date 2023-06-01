package com.yishuifengxiao.common.social;

import com.yishuifengxiao.common.security.httpsecurity.AuthorizeProvider;
import com.yishuifengxiao.common.security.support.PropertyResource;
import com.yishuifengxiao.common.security.support.AuthenticationPoint;
import com.yishuifengxiao.common.social.qq.QQSocialProvider;
import com.yishuifengxiao.common.social.support.CompositeOAuth2AccessTokenResponseClient;
import com.yishuifengxiao.common.social.support.CompositeOAuth2UserService;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SocialAuthorizeProvider implements AuthorizeProvider, InitializingBean {


    private List<SocialProvider> providers = new ArrayList<>();

    @SuppressWarnings({ "unused", "deprecation" })
    @Override
    public void apply(PropertyResource propertyResource, AuthenticationPoint authenticationPoint, HttpSecurity http) throws Exception {
        /**
         * 用于OAuth 2.0登录的AbstractHttpConfigurer，它利用OAuth 2.0Authorization Code Grant Flow。
         * OAuth 2.0登录为应用程序提供了一种功能，可以让用户使用OAuth 1.0或OpenID Connect 1.0提供程序中的现有帐户登录。
         * 所有配置选项都提供了默认值，唯一需要的配置是clientRegistrationRepository（ClientRegistration Repository）。或者，可以注册ClientRegistrationRepository@Bean。
         */
        //默认情况下，OAuth 2.0登录页面由DefaultLoginPageGeneratingFilter自动生成。
        // 默认登录页面显示每个已配置的OAuth客户端及其ClientRegistration.clientName作为链接，该链接能够启动授权请求（或OAuth 2.0登录）。

        //为了让DefaultLoginPageGeneratingFilter显示配置的OAuth客户端的链接，注册的ClientRegistrationRepository还需要实现Iterable＜ClientRegistration＞。请参阅InMemoryClientRegistryRepository以获取参考。

        final OAuth2LoginConfigurer<HttpSecurity> oauth2Login = http.oauth2Login();

        oauth2Login.failureHandler(authenticationPoint)
                .successHandler(authenticationPoint)
                .loginPage(propertyResource.security().getLoginPage());
//        The default redirect URI template is . The registrationId is a unique identifier for the . {baseUrl}/login/oauth2/code/{registrationId}ClientRegistration
//                .loginProcessingUrl(propertyResource.security().getFormActionUrl());


        // The link’s destination for each OAuth Client defaults to the following:
        // OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/{registrationId}"
        // The following line shows an example:
        //
        //<a href="/oauth2/authorization/google">Google</a>
        //To override the default login page, configure oauth2Login().loginPage() and (optionally) oauth2Login().authorizationEndpoint().baseUri().

//        The default redirect URI template is {baseUrl}/login/oauth2/code/{registrationId}. The registrationId is a unique identifier for the ClientRegistration.
        //    .authorizationEndpoint(authorization -> authorization
        //                    .baseUri("/login/oauth2/authorization")

        //
        //As noted earlier, configuring oauth2Login().authorizationEndpoint().baseUri() is optional. However, if you choose to customize it, ensure the link to each OAuth Client matches the authorizationEndpoint().baseUri().
        //
        //The following line shows an example:
        //
        //<a href="/login/oauth2/authorization/google">Google</a>
        //由客户端用于通过用户代理重定向从资源所有者获得授权
        OAuth2LoginConfigurer<HttpSecurity>.AuthorizationEndpointConfig authorizationEndpoint = oauth2Login.authorizationEndpoint();


        //客户端用于交换访问令牌的授权授权，通常与客户端身份验证一起使用。
        // 使用CompositeOAuth2AccessTokenResponseClient
        oauth2Login.tokenEndpoint()
                .accessTokenResponseClient(this.accessTokenResponseClient(providers));

        //是一个OAuth 2.0受保护资源，它返回有关经过身份验证的最终用户的声明。
        // 为了获得有关最终用户的请求声明，客户端使用通过OpenID Connect Authentication获得的访问令牌向UserInfo Endpoint发出请求。
        // 这些声明通常由包含声明的名称-值对集合的JSON对象表示。
        OAuth2LoginConfigurer<HttpSecurity>.UserInfoEndpointConfig userService = oauth2Login
                .userInfoEndpoint()
                .userService(oauth2UserService(providers));

        for (SocialProvider provider : providers) {
            userService.customUserType(provider.customUserType(), provider.clientRegistrationId());
        }
        // 可选，要保证与redirect-uri-template匹配
        //由授权服务器使用，通过资源所有者用户代理向客户端返回包含授权凭据的响应
        //授权服务器使用重定向端点通过资源所有者用户代理向客户端返回授权响应（包含授权凭据）。
        //OAuth 2.0登录利用授权代码授予。因此，授权凭证就是授权代码。
        OAuth2LoginConfigurer<HttpSecurity>.RedirectionEndpointConfig redirectionEndpoint = oauth2Login.redirectionEndpoint();

        //The default Authorization Response baseUri (redirection endpoint) is /login/oauth2/code/*, which is defined in OAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI.
        //
        //If you would like to customize the Authorization Response baseUri, configure it as shown in the following example:
        // .redirectionEndpoint(redirection -> redirection
        //                    .baseUri("/login/oauth2/callback/*")

    }

    private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient(List<SocialProvider> providers) {
        CompositeOAuth2AccessTokenResponseClient client = new CompositeOAuth2AccessTokenResponseClient();
        // 加入自定义QQOAuth2AccessTokenResponseClient
        for (SocialProvider provider : providers) {
            client.add(provider.clientRegistrationId(), provider.accessTokenResponseClient());
        }
        return client;
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(List<SocialProvider> providers) {
        CompositeOAuth2UserService service = new CompositeOAuth2UserService();
        // 加入自定义QQOAuth2UserService
        for (SocialProvider provider : providers) {
            service.add(provider.clientRegistrationId(), provider.userService());
        }
        return service;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<SocialProvider> providers = DataUtil.stream(this.providers).filter(Objects::nonNull).filter(v -> StringUtils.isBlank(v.clientRegistrationId()) || null == v.customUserType()).collect(Collectors.toList());
        QQSocialProvider provider = new QQSocialProvider();
        if (!providers.stream().anyMatch(v -> StringUtils.equalsIgnoreCase(v.clientRegistrationId(), provider.clientRegistrationId()))) {
            providers.add(provider);
        }
        this.providers = providers;
    }

    public void setProviders(List<SocialProvider> providers) {
        this.providers = providers;
    }
}
