package com.yishuifengxiao.common.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.security.oauth2.enhancer.CustomeTokenEnhancer;

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
	
	
	@ConditionalOnMissingBean(name = { "tokenStore" })
	@Bean("tokenStore")
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	/**
	 * 生成自定义token
	 * 
	 * @return
	 */
	@Bean("customeTokenEnhancer")
	@ConditionalOnMissingBean(name = "customeTokenEnhancer")
	public TokenEnhancer tokenEnhancer() {
		return new CustomeTokenEnhancer();
	}

	/**
	 * Basic interface for determining whether a given client authentication request
	 * has been approved by the current user. 【认证服务器中需要显示使用到】
	 * 
	 * @param tokenStore
	 * @return
	 */
	@Bean
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore,
			ClientDetailsService clientDetailsService) {
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}

	/**
	 * Interface for saving, retrieving and revoking user approvals (per client, per
	 * scope).
	 * 
	 * @param tokenStore
	 * @return
	 * @throws Exception
	 */
	@Bean
	public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}

	@Bean("customClientDetailsService")
	public ClientDetailsService customClientDetailsService(PasswordEncoder passwordEncoder){
		ClientDetailsServiceImpl  customClientDetailsService=new	ClientDetailsServiceImpl();
		customClientDetailsService.setPasswordEncoder(passwordEncoder);
		return  customClientDetailsService;
	}

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

	/**
	 * 决定是否授权
	 */
	@Autowired
	@Qualifier("customClientDetailsService")
	private ClientDetailsService customClientDetailsService;

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
		// @formatter:on

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
	}

}