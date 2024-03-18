package com.yishuifengxiao.common.oauth2server.token;

import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.*;

/**
 * @author qingteng
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleOAuth2TokenGenerator implements OAuth2TokenGenerator<OAuth2Token> {
    private final DelegatingOAuth2TokenGenerator delegatingOAuth2TokenGenerator;

    public SimpleOAuth2TokenGenerator(JwtEncoder jwtEncoder) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(new JwtTokeCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        accessTokenGenerator.setAccessTokenCustomizer(new OAuth2AccessTokeCustomizer());
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        this.delegatingOAuth2TokenGenerator = new DelegatingOAuth2TokenGenerator(
                jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    @Override
    public OAuth2Token generate(OAuth2TokenContext context) {
        return this.delegatingOAuth2TokenGenerator.generate(context);
    }
}
