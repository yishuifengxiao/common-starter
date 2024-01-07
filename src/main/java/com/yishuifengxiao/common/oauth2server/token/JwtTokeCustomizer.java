package com.yishuifengxiao.common.oauth2server.token;

import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 21:40
 * @since 1.0.0
 */
public class JwtTokeCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Override
    public void customize(JwtEncodingContext context) {
        JwsHeader.Builder headers = context.getJwsHeader();
        JwtClaimsSet.Builder claims = context.getClaims();
        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            // Customize headers/claims for access_token

        } else if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
            // Customize headers/claims for id_token

        }
    }
}
