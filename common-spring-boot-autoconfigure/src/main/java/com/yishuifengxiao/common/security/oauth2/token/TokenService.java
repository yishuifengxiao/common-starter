package com.yishuifengxiao.common.security.oauth2.token;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 自定义token生成策略
 * 
 * @author yishui
 * @date 2019年12月24日
 * @version 1.0.0
 */
public interface TokenService {
	/**
	 * But the refresh token itself might need to be re-issued if it has expired.
	 * 
	 * @param tokenStore
	 * @param authenticationManager
	 * @param clientDetailsService
	 * @param authentication
	 * @param refreshToken
	 * @return
	 * @throws AuthenticationException
	 */
	OAuth2AccessToken createAccessToken(TokenStore tokenStore, AuthenticationManager authenticationManager,
			ClientDetailsService clientDetailsService, OAuth2Authentication authentication,
			OAuth2RefreshToken refreshToken) throws AuthenticationException;

	/**
	 * Only create a new refresh token if there wasn't an existing one associated
	 * with an expired access token.Clients might be holding existing refresh
	 * tokens, so we re-use it in the case that the old access token expired<br/>
	 * 仅当没有与过期的访问令牌关联的现有令牌时才创建一个新的刷新令牌。客户端可能持有现有的刷新令牌，因此如果旧的访问令牌已过期，我们将重新使用它
	 * 
	 * @param tokenStore
	 * @param authenticationManager
	 * @param clientDetailsService
	 * @param authentication
	 * @return
	 * @throws AuthenticationException
	 */
	OAuth2RefreshToken createRefreshToken(TokenStore tokenStore, AuthenticationManager authenticationManager,
			ClientDetailsService clientDetailsService, OAuth2Authentication authentication)
			throws AuthenticationException;

	/**
	 * Create a refreshed authentication.
	 * 
	 * @param tokenStore
	 * @param authenticationManager
	 * @param clientDetailsService
	 * @param authentication        The authentication.
	 * @param request               The scope for the refreshed token.
	 * @return The refreshed authentication.
	 * @throws AuthenticationException If the scope requested is invalid or wider than
	 *                               the original scope.
	 */
	OAuth2Authentication createRefreshedAuthentication(TokenStore tokenStore,
			AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService,
			OAuth2Authentication authentication, TokenRequest tokenRequest) throws AuthenticationException;

	/**
	 * 是否支持刷新token操作
	 * 
	 * @return true表示支持刷新token,false表示不支持
	 */
	boolean supportRefreshToken();

	/**
	 * 是否支持在原始刷新token未过期时不存储新产生的刷新token
	 * 
	 * @return true表示原始刷新token未过期时不存储新产生的刷新token
	 */
	boolean reuseRefreshToken();
}
