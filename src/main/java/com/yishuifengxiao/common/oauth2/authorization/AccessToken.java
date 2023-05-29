package com.yishuifengxiao.common.oauth2.authorization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccessToken implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4604869630498772717L;
	private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    private String authorizedScopes;
    private String attributes;
    private String state;
    private String authorizationCodeValue;
    private Long authorizationCodeIssuedAt;
    private Long authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    private String accessTokenValue;
    private Long accessTokenIssuedAt;
    private Long accessTokenExpiresAt;
    private String accessTokenMetadata;
    private String accessTokenType;
    private String accessTokenScopes;
    private String oidcIdTokenValue;
    private Long oidcIdTokenIssuedAt;
    private Long oidcIdTokenExpiresAt;
    private String oidcIdTokenMetadata;
    private String refreshTokenValue;
    private Long refreshTokenIssuedAt;
    private Long refreshTokenExpiresAt;
    private String refreshTokenMetadata;

}
