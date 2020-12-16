package com.yishuifengxiao.common.oauth2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.yishuifengxiao.common.oauth2.filter.TokenEndpointAuthenticationFilter;
import com.yishuifengxiao.common.oauth2.provider.TokenStrategy;

/**
 * Configuration for a Spring Security OAuth2 authorization server. Back off if
 * another {@link AuthorizationServerConfigurer} already exists or if
 * authorization server is not enabled.
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @since 1.3.0
 */
public class Oauth2Server extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("tokenStore")
	private TokenStore tokenStore;

	@Autowired
	private Oauth2Properties properties;

	/**
	 * 授权管理器，在spring security里注入的
	 */
	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	/**
	 * 决定是否授权【具体定义参见Oauth2Config】
	 */
	@Autowired
	private UserApprovalHandler userApprovalHandler;

	@Autowired
	private TokenEnhancer customeTokenEnhancer;

	@Autowired
	@Qualifier("authWebResponseExceptionTranslator")
	private WebResponseExceptionTranslator<OAuth2Exception> authWebResponseExceptionTranslator;

	/**
	 * 定义在security-core包中
	 */
	@Autowired
	@Qualifier("exceptionAuthenticationEntryPoint")
	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	/**
	 * 决定是否授权
	 */
	@Autowired
	@Qualifier("customClientDetailsService")
	private ClientDetailsService customClientDetailsService;

	/**
	 * token生成器，负责token的生成或获取
	 */
	@Autowired
	@Qualifier("tokenStrategy")
	private TokenStrategy tokenStrategy;

	@Autowired
	@Qualifier("tokenEndpointAuthenticationFilter")
	private TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter;

	@Autowired
	@Qualifier("userDetailsService") 
	private UserDetailsService userDetailsService;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		clients.withClientDetails(customClientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// @formatter:off
		endpoints
		    .userApprovalHandler(userApprovalHandler)
			.tokenStore(tokenStore)
			.authenticationManager(authenticationManager);
		
		// 增强器链
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
		tokenEnhancers.add(customeTokenEnhancer);
		tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
		
		//加入到增强器链中
		endpoints
			.tokenEnhancer(tokenEnhancerChain);
		
		//配置token的生成规则
      endpoints.tokenServices(this.addUserDetailsService(this.tokenStrategy,this.userDetailsService));
      
      endpoints.exceptionTranslator(authWebResponseExceptionTranslator);
		// @formatter:on

	}

	/**
	 * 增加provider，否则刷新token时会出错
	 * 
	 * @param tokenServices
	 * @param userDetailsService
	 */
	private TokenStrategy addUserDetailsService(TokenStrategy tokenStrategy, UserDetailsService userDetailsService) {
		if (userDetailsService != null) {
			PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
			provider.setPreAuthenticatedUserDetailsService(
					new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>(userDetailsService));
			tokenStrategy
					.setAuthenticationManager(new ProviderManager(Arrays.<AuthenticationProvider>asList(provider)));
		}
		return tokenStrategy;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		if (this.properties.getCheckTokenAccess() != null) {
			security.checkTokenAccess(this.properties.getCheckTokenAccess());
		}
		if (this.properties.getTokenKeyAccess() != null) {
			security.tokenKeyAccess(this.properties.getTokenKeyAccess());
		}
		if (this.properties.getRealm() != null) {
			security.realm(this.properties.getRealm());
		}
		security.authenticationEntryPoint(exceptionAuthenticationEntryPoint);
		// Adds a new custom authentication filter for the TokenEndpoint.
		security.addTokenEndpointAuthenticationFilter(tokenEndpointAuthenticationFilter);

		security.allowFormAuthenticationForClients();
	}

}