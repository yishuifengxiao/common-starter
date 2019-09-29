package com.yishuifengxiao.common.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yishuifengxiao.common.security.extractor.CustomTokenExtractor;
import com.yishuifengxiao.common.security.oauth2.enhancer.CustomeTokenEnhancer;
import com.yishuifengxiao.common.security.service.ClientDetailsServiceImpl;
import com.yishuifengxiao.common.security.utils.TokenUtils;

@Configuration
@ConditionalOnClass({ OAuth2AccessToken.class, WebMvcConfigurer.class })
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class OAuth2ExtendAutoConfiguration {

	@ConditionalOnMissingBean(name = { "tokenStore" })
	@Bean("tokenStore")
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	/**
	 * 必须加入，不然自定义权限表达式不生效
	 * 
	 * @param applicationContext
	 * @return
	 */
	@Bean
	public DefaultWebSecurityExpressionHandler expressionHandler(ApplicationContext applicationContext) {
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;

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
	 * 自定义token提取器
	 * 
	 * @return
	 */
	@Bean("tokenExtractor")
	@ConditionalOnMissingBean(name = "tokenExtractor")
	public TokenExtractor tokenExtractor() {
		return new CustomTokenExtractor();
	}

	@Bean
	@ConditionalOnMissingBean
	public TokenUtils tokenUtils(ClientDetailsService clientDetailsService,
			AuthorizationServerTokenServices authorizationServerTokenServices, UserDetailsService userDetailsService) {
		TokenUtils tokenUtils = new TokenUtils();
		tokenUtils.setClientDetailsService(clientDetailsService);
		tokenUtils.setUserDetailsService(userDetailsService);
		tokenUtils.setAuthorizationServerTokenServices(authorizationServerTokenServices);
		return tokenUtils;
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
	@ConditionalOnMissingBean(name = "customClientDetailsService")
	public ClientDetailsService customClientDetailsService(PasswordEncoder passwordEncoder) {
		ClientDetailsServiceImpl customClientDetailsService = new ClientDetailsServiceImpl();
		customClientDetailsService.setPasswordEncoder(passwordEncoder);
		return customClientDetailsService;
	}

}
