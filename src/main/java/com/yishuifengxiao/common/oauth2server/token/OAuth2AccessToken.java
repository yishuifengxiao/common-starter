package com.yishuifengxiao.common.oauth2server.token;

import com.yishuifengxiao.common.security.token.SecurityToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Token;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author qingteng
 * @version 1.0.0
 * @date 2024/1/7 21:26
 * @since 1.0.0
 */
public class OAuth2AccessToken extends SecurityToken implements Serializable, OAuth2Token {


    public OAuth2AccessToken(String principal, String deviceId, Integer validSeconds, Collection<? extends GrantedAuthority> authorities) {
        super(principal, deviceId, validSeconds, authorities);
    }

    public OAuth2AccessToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public String getTokenValue() {
        return this.getValue();
    }
}
