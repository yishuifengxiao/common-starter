package com.yishuifengxiao.common.security.oauth2.token;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

import com.yishuifengxiao.common.security.event.TokenGenerateEvent;
import com.yishuifengxiao.common.security.event.TokenRemoveEvent;

/**
 * token处理策略<br/>
 * 替换系统默认的token处理策略
 * 
 * @author yishui
 * @date 2019年12月24日
 * @version 1.0.0
 */
public class TokenStrategy
		implements AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices {

	protected TokenStore tokenStore;

	protected ClientDetailsService clientDetailsService;

	private TokenEnhancer accessTokenEnhancer;

	private AuthenticationManager authenticationManager;

	private ApplicationContext context;

	private TokenService tokenService;

	/**
	 * Create an access token associated with the specified credentials.
	 * 
	 * @param authentication The credentials associated with the access token.
	 * @return The access token.
	 * @throws AuthenticationException If the credentials are inadequate.
	 * @see AuthorizationServerTokenServices
	 */
	@Transactional
	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {

		OAuth2AccessToken existingAccessToken = tokenStore.getAccessToken(authentication);
		OAuth2RefreshToken refreshToken = null;
		if (existingAccessToken != null) {
			if (existingAccessToken.isExpired()) {
				// 原始token已经过期
				if (existingAccessToken.getRefreshToken() != null) {
					refreshToken = existingAccessToken.getRefreshToken();
					// The token store could remove the refresh token when the
					// access token is removed, but we want to
					// be sure...
					tokenStore.removeRefreshToken(refreshToken);
				}
				tokenStore.removeAccessToken(existingAccessToken);
				context.publishEvent(new TokenRemoveEvent(existingAccessToken));
			} else {
				// 原始token未过期
				// Re-store the access token in case the authentication has changed
				tokenStore.storeAccessToken(existingAccessToken, authentication);
				context.publishEvent(new TokenGenerateEvent(existingAccessToken, authentication, true));
				return existingAccessToken;
			}
		}

		// Only create a new refresh token if there wasn't an existing one
		// associated with an expired access token.
		// Clients might be holding existing refresh tokens, so we re-use it in
		// the case that the old access token
		// expired.
		if (refreshToken == null) {
			refreshToken = tokenService.createRefreshToken(tokenStore, authenticationManager, clientDetailsService,
					authentication);
		}
		// But the refresh token itself might need to be re-issued if it has
		// expired.
		else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
			ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
			if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
				refreshToken = tokenService.createRefreshToken(tokenStore, authenticationManager, clientDetailsService,
						authentication);
			}
		}

		OAuth2AccessToken accessToken = createNewAccessToken(authentication, refreshToken);

		tokenStore.storeAccessToken(accessToken, authentication);
		// In case it was modified
		refreshToken = accessToken.getRefreshToken();
		if (refreshToken != null) {
			tokenStore.storeRefreshToken(refreshToken, authentication);
		}

		return accessToken;

	}

	/**
	 * Refresh an access token. The authorization request should be used for 2
	 * things (at least): to validate that the client id of the original access
	 * token is the same as the one requesting the refresh, and to narrow the scopes
	 * (if provided).
	 * 
	 * @param refreshToken The details about the refresh token.
	 * @param tokenRequest The incoming token request.
	 * @return The (new) access token.
	 * @throws AuthenticationException If the refresh token is invalid or expired.
	 * @see AuthorizationServerTokenServices
	 */
	@Transactional(noRollbackFor = { InvalidTokenException.class, InvalidGrantException.class })
	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
			throws AuthenticationException {

		if (!tokenService.supportRefreshToken()) {
			throw new InvalidGrantException(refreshTokenValue + " Does not support refresh  operation ");
		}

		OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refreshTokenValue);
		if (refreshToken == null) {
			throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
		}
		OAuth2Authentication authentication = readAuthenticationForRefreshToken(refreshToken, tokenRequest);

		// clear out any access tokens already associated with the refresh
		// token.
		tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);

		if (isExpired(refreshToken)) {
			tokenStore.removeRefreshToken(refreshToken);
			throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
		}

		authentication = tokenService.createRefreshedAuthentication(tokenStore, authenticationManager,
				clientDetailsService, authentication, tokenRequest);

		if (!tokenService.reuseRefreshToken()) {
			tokenStore.removeRefreshToken(refreshToken);
			refreshToken = tokenService.createRefreshToken(tokenStore, authenticationManager, clientDetailsService,
					authentication);
		}

		// 根据刷新token生成一个新的token
		OAuth2AccessToken accessToken = createNewAccessToken(authentication, refreshToken);

		tokenStore.storeAccessToken(accessToken, authentication);

		if (!tokenService.reuseRefreshToken()) {
			tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
		}
		return accessToken;
	}

	/**
	 * 根据刷新token获取到认证信息
	 * 
	 * @param refreshTokenValue
	 * @param tokenRequest
	 * @return
	 */
	private OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken,
			TokenRequest tokenRequest) {

		OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(refreshToken);
		if (this.authenticationManager != null && !authentication.isClientOnly()) {
			// The client has already been authenticated, but the user authentication might
			// be old now, so give it a
			// chance to re-authenticate.
			Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUserAuthentication(), "",
					authentication.getAuthorities());
			user = authenticationManager.authenticate(user);
			Object details = authentication.getDetails();
			authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
			authentication.setDetails(details);
		}
		String clientId = authentication.getOAuth2Request().getClientId();
		if (clientId == null || !clientId.equals(tokenRequest.getClientId())) {
			throw new InvalidGrantException("Wrong client for this refresh token: " + refreshToken.getValue());
		}
		return authentication;
	}

	/**
	 * Retrieve an access token stored against the provided authentication key, if
	 * it exists.
	 * 
	 * @param authentication the authentication key for the access token
	 * 
	 * @return the access token or null if there was none
	 * 
	 * @see AuthorizationServerTokenServices
	 */
	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		return tokenStore.getAccessToken(authentication);
	}

	/**
	 * 根据认证信息和刷新token生成一个新的token
	 * 
	 * @param authentication
	 * @param refreshToken
	 * @return
	 */
	private OAuth2AccessToken createNewAccessToken(OAuth2Authentication authentication,
			OAuth2RefreshToken refreshToken) {
		OAuth2AccessToken accessToken = tokenService.createAccessToken(tokenStore, authenticationManager,
				clientDetailsService, authentication, refreshToken);
		// 对新生成的token进行处理
		accessToken = accessTokenEnhancer != null ? accessTokenEnhancer.enhance(accessToken, authentication)
				: accessToken;
		context.publishEvent(new TokenGenerateEvent(accessToken, authentication, false));
		return accessToken;
	}

	/**
	 * 刷新token是否已经过期
	 * 
	 * @param refreshToken
	 * @return
	 */
	protected boolean isExpired(OAuth2RefreshToken refreshToken) {
		if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
			ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
			return expiringToken.getExpiration() == null
					|| System.currentTimeMillis() > expiringToken.getExpiration().getTime();
		}
		return false;
	}

	/**
	 * Load the credentials for the specified access token.
	 *
	 * @param accessToken The access token value.
	 * @return The authentication for the access token.
	 * @throws AuthenticationException If the access token is expired
	 * @throws InvalidTokenException   if the token isn't valid
	 * @see ResourceServerTokenServices
	 */
	@Override
	public OAuth2Authentication loadAuthentication(String accessTokenValue)
			throws AuthenticationException, InvalidTokenException {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
		if (accessToken == null) {
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		} else if (accessToken.isExpired()) {
			tokenStore.removeAccessToken(accessToken);
			throw new InvalidTokenException("Access token expired: " + accessTokenValue);
		}

		OAuth2Authentication result = tokenStore.readAuthentication(accessToken);
		if (result == null) {
			// in case of race condition
			throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
		}
		if (clientDetailsService != null) {
			String clientId = result.getOAuth2Request().getClientId();
			try {
				clientDetailsService.loadClientByClientId(clientId);
			} catch (ClientRegistrationException e) {
				throw new InvalidTokenException("Client not valid: " + clientId, e);
			}
		}
		return result;
	}

	/**
	 * Retrieve the full access token details from just the value.
	 * 
	 * @param accessToken the token value
	 * @return the full access token with client id etc.
	 * @see ResourceServerTokenServices
	 */
	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		return tokenStore.readAccessToken(accessToken);
	}

	@Override
	public boolean revokeToken(String tokenValue) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
		if (accessToken == null) {
			return false;
		}
		if (accessToken.getRefreshToken() != null) {
			tokenStore.removeRefreshToken(accessToken.getRefreshToken());
		}
		tokenStore.removeAccessToken(accessToken);
		context.publishEvent(new TokenRemoveEvent(accessToken));
		return true;
	}

	public String getClientId(String tokenValue) {
		OAuth2Authentication authentication = tokenStore.readAuthentication(tokenValue);
		if (authentication == null) {
			throw new InvalidTokenException("Invalid access token: " + tokenValue);
		}
		OAuth2Request clientAuth = authentication.getOAuth2Request();
		if (clientAuth == null) {
			throw new InvalidTokenException("Invalid access token (no client id): " + tokenValue);
		}
		return clientAuth.getClientId();
	}

	/**
	 * The persistence strategy for token storage.
	 * 
	 * @param tokenStore the store for access and refresh tokens.
	 */
	public void setTokenStore(TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	/**
	 * An authentication manager that will be used (if provided) to check the user
	 * authentication when a token is refreshed.
	 * 
	 * @param authenticationManager the authenticationManager to set
	 */
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * The client details service to use for looking up clients (if necessary).
	 * Optional if the access token expiry is set globally via
	 * {@link #setAccessTokenValiditySeconds(int)}.
	 * 
	 * @param clientDetailsService the client details service
	 */
	public void setClientDetailsService(ClientDetailsService clientDetailsService) {
		this.clientDetailsService = clientDetailsService;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * An access token enhancer that will be applied to a new token before it is
	 * saved in the token store.
	 * 
	 * @param accessTokenEnhancer the access token enhancer to set
	 */
	public void setTokenEnhancer(TokenEnhancer accessTokenEnhancer) {
		this.accessTokenEnhancer = accessTokenEnhancer;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	public TokenStrategy(TokenStore tokenStore, ClientDetailsService clientDetailsService,
			TokenEnhancer accessTokenEnhancer, ApplicationContext context) {
		this.tokenStore = tokenStore;
		this.clientDetailsService = clientDetailsService;
		this.accessTokenEnhancer = accessTokenEnhancer;
		this.context = context;
	}

}
