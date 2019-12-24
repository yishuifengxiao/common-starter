package com.yishuifengxiao.common.security.oauth2.token;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.yishuifengxiao.common.tool.random.UID;

/**
 * 默认实现的自定义token生成策略
 * 
 * @author yishui
 * @date 2019年12月24日
 * @version 1.0.0
 */
public class SimpleTokenService implements TokenService, InitializingBean {

	private int refreshTokenValiditySeconds = 60 * 60 * 24 * 30; // default 30 days.

	private int accessTokenValiditySeconds = 60 * 60 * 12; // default 12 hours.

	/**
	 * Create a refreshed authentication.
	 * 
	 * @param authentication The authentication.
	 * @param request        The scope for the refreshed token.
	 * @return The refreshed authentication.
	 * @throws InvalidScopeException If the scope requested is invalid or wider than
	 *                               the original scope.
	 */
	@Override
	public OAuth2Authentication createRefreshedAuthentication(TokenStore tokenStore,
			AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService,
			OAuth2Authentication authentication, TokenRequest tokenRequest) {
		OAuth2Authentication narrowed = authentication;
		Set<String> scope = tokenRequest.getScope();
		OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(tokenRequest);
		if (scope != null && !scope.isEmpty()) {
			Set<String> originalScope = clientAuth.getScope();
			if (originalScope == null || !originalScope.containsAll(scope)) {
				throw new InvalidScopeException(
						"Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
			} else {
				clientAuth = clientAuth.narrowScope(scope);
			}
		}
		narrowed = new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
		return narrowed;
	}

	/**
	 * 仅当没有与过期的访问令牌关联的现有令牌时才创建一个新的刷新令牌。客户端可能持有现有的刷新令牌，因此如果旧的访问令牌已过期，我们将重新使用它
	 */
	@Override
	public OAuth2RefreshToken createRefreshToken(TokenStore tokenStore, AuthenticationManager authenticationManager,
			ClientDetailsService clientDetailsService, OAuth2Authentication authentication)
			throws AuthenticationException {
		if (!isSupportRefreshToken(clientDetailsService, authentication.getOAuth2Request())) {
			return null;
		}
		int validitySeconds = getRefreshTokenValiditySeconds(clientDetailsService, authentication.getOAuth2Request());
		String value = UID.uuid();
		if (validitySeconds > 0) {
			return new DefaultExpiringOAuth2RefreshToken(value,
					new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
		}
		return new DefaultOAuth2RefreshToken(value);
	}

	/**
	 * Is a refresh token supported for this client (or the global setting if
	 * {@link #setClientDetailsService(ClientDetailsService) clientDetailsService}
	 * is not set.
	 * 
	 * @param clientAuth the current authorization request
	 * @return boolean to indicate if refresh token is supported
	 */
	protected boolean isSupportRefreshToken(ClientDetailsService clientDetailsService, OAuth2Request clientAuth) {
		if (clientDetailsService != null) {
			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			return client.getAuthorizedGrantTypes().contains("refresh_token");
		}
		return this.supportRefreshToken();
	}

	/**
	 * 生成token
	 */
	@Override
	public OAuth2AccessToken createAccessToken(TokenStore tokenStore, AuthenticationManager authenticationManager,
			ClientDetailsService clientDetailsService, OAuth2Authentication authentication,
			OAuth2RefreshToken refreshToken) throws AuthenticationException {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
		int validitySeconds = getAccessTokenValiditySeconds(clientDetailsService, authentication.getOAuth2Request());
		if (validitySeconds > 0) {
			token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
		}
		token.setRefreshToken(refreshToken);
		token.setScope(authentication.getOAuth2Request().getScope());

		return token;
	}

	/**
	 * The access token validity period in seconds
	 * 
	 * @param clientAuth the current authorization request
	 * @return the access token validity period in seconds
	 */
	protected int getAccessTokenValiditySeconds(ClientDetailsService clientDetailsService, OAuth2Request clientAuth) {
		if (clientDetailsService != null) {
			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			Integer validity = client.getAccessTokenValiditySeconds();
			if (validity != null) {
				return validity;
			}
		}
		return accessTokenValiditySeconds;
	}

	/**
	 * The refresh token validity period in seconds
	 * 
	 * @param clientAuth the current authorization request
	 * @return the refresh token validity period in seconds
	 */
	protected int getRefreshTokenValiditySeconds(ClientDetailsService clientDetailsService, OAuth2Request clientAuth) {
		if (clientDetailsService != null) {
			ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
			Integer validity = client.getRefreshTokenValiditySeconds();
			if (validity != null) {
				return validity;
			}
		}
		return refreshTokenValiditySeconds;
	}

	/**
	 * The validity (in seconds) of the refresh token. If less than or equal to zero
	 * then the tokens will be non-expiring.
	 * 
	 * @param refreshTokenValiditySeconds The validity (in seconds) of the refresh
	 *                                    token.
	 */
	public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	/**
	 * The default validity (in seconds) of the access token. Zero or negative for
	 * non-expiring tokens. If a client details service is set the validity period
	 * will be read from the client, defaulting to this value if not defined by the
	 * client.
	 * 
	 * @param accessTokenValiditySeconds The validity (in seconds) of the access
	 *                                   token.
	 */
	public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public boolean supportRefreshToken() {
		return true;
	}

	@Override
	public boolean reuseRefreshToken() {
		return true;
	}

}
