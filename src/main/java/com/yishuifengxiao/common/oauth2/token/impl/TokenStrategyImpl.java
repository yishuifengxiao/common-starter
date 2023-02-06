package com.yishuifengxiao.common.oauth2.token.impl;

import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;


import com.yishuifengxiao.common.oauth2.token.AccessToken;
import com.yishuifengxiao.common.oauth2.token.TokenStrategy;
import com.yishuifengxiao.common.tool.datetime.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * token处理策略
 * </p>
 * 替换系统默认的token处理策略
 * <ul>
 * <li>该策略被先被<code>Oauth2Resource</code>收集，经过<code>public void configure(ResourceServerSecurityConfigurer resources)</code>注入到oauth2中</li>
 * </ul>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
@Slf4j
public class TokenStrategyImpl extends TokenStrategy {

    protected TokenStore tokenStore;

    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue)
            throws AuthenticationException, InvalidTokenException {

        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】(OAuth2AccessToken新请求) 无效的token {}", accessTokenValue);
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else if (accessToken.isExpired()) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】(OAuth2AccessToken新请求) 过期的token {}", accessTokenValue);
            tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
        if (result == null) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】(OAuth2AccessToken新请求) 认证无效导致token {} 无效", accessTokenValue);
            // in case of race condition
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        }

        // 每次访问时重置token的过期时间
        this.refreshAccessToken(result);

        return result;
    }

    /**
     * 根据OAuth2Authentication 重置OAuth2AccessToken的过期时间
     *
     * @param authentication 认证信息
     */
    private void refreshAccessToken(OAuth2Authentication authentication) {
        if (null == authentication) {
            return;
        }
        OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.getAccessToken(authentication);

        if (null != oAuth2AccessToken && !oAuth2AccessToken.isExpired()) {
            // 如果OAuth2AccessToken存在且未过期，就重置其过期时间
            oAuth2AccessToken = this.refreshExpireTime(oAuth2AccessToken);
        }
        if (null != oAuth2AccessToken) {
            // 覆盖旧的token
            this.tokenStore.storeAccessToken(oAuth2AccessToken, authentication);
        }
    }

    /**
     * 重置token的过期时间
     *
     * @param existingAccessToken OAuth2AccessToken
     * @return 重置过期时间后的OAuth2AccessToken
     */
    private OAuth2AccessToken refreshExpireTime(OAuth2AccessToken oAuth2AccessToken) {
        // 重新设置token的过期时间
        if (oAuth2AccessToken instanceof AccessToken) {
            AccessToken token = (AccessToken) oAuth2AccessToken;
            // 获取token的有效时间
            Integer expireInSeconds = token.getExpireInSeconds();
            // 判断获取的token的有效时间有效性
            if (null == expireInSeconds || expireInSeconds <= 0) {
                expireInSeconds = AccessToken.DEFAULT_EXPIRE_TIME_IN_SECONDS;
            }
            // 重新设置token的过期时间，以当前的时间点重新开始计时
            token.setExpiration(
                    DateTimeUtil.localDateTime2Date(LocalDateTime.now().plusSeconds(expireInSeconds.longValue())));
            oAuth2AccessToken = token;
        }
        return oAuth2AccessToken;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        return tokenStore.readAccessToken(accessToken);
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public AuthorizationServerTokenServices getAuthorizationServerTokenServices() {
        return authorizationServerTokenServices;
    }

    public void setAuthorizationServerTokenServices(AuthorizationServerTokenServices authorizationServerTokenServices) {
        this.authorizationServerTokenServices = authorizationServerTokenServices;
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

}
