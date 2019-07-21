package com.yishuifengxiao.common.security.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.yishuifengxiao.common.security.oauth2.enhancer.CustomeTokenEnhancer;
import com.yishuifengxiao.common.security.service.ClientDetailsServiceImpl;

public class OAuth2Config {

	
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
	@ConditionalOnMissingBean(name="customClientDetailsService")
	public ClientDetailsService customClientDetailsService(PasswordEncoder passwordEncoder){
		ClientDetailsServiceImpl  customClientDetailsService=new	ClientDetailsServiceImpl();
		customClientDetailsService.setPasswordEncoder(passwordEncoder);
		return  customClientDetailsService;
	}

	
	@Configuration
	public class OAuth2ResourceConfig extends Oauth2Resource {
	}

	@Configuration
	public class OAuth2ServerConfig extends Oauth2Server {
	}
}
