package com.yishuifengxiao.common.oauth2;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yishuifengxiao.common.oauth2.autoconfigure.SecurityRedisAutoConfiguration;
import com.yishuifengxiao.common.oauth2.enhancer.CustomeTokenEnhancer;
import com.yishuifengxiao.common.oauth2.extractor.CustomTokenExtractor;
import com.yishuifengxiao.common.oauth2.filter.TokenEndpointAuthenticationFilter;
import com.yishuifengxiao.common.oauth2.filter.TokenEndpointFilter;
import com.yishuifengxiao.common.oauth2.interceptor.TokenEndpointInterceptor;
import com.yishuifengxiao.common.oauth2.provider.TokenStrategy;
import com.yishuifengxiao.common.oauth2.service.ClientDetailsServiceImpl;
import com.yishuifengxiao.common.oauth2.support.TokenUtils;
import com.yishuifengxiao.common.oauth2.token.TokenService;
import com.yishuifengxiao.common.oauth2.token.SimpleTokenService;
import com.yishuifengxiao.common.oauth2.translator.Auth2ResponseExceptionTranslator;
import com.yishuifengxiao.common.oauth2.translator.AuthWebResponseExceptionTranslator;
import com.yishuifengxiao.common.security.AbstractSecurityConfig;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.support.ErrorMsgUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 注入oauth2相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass({ OAuth2AccessToken.class, WebMvcConfigurer.class })
@ConditionalOnBean(AbstractSecurityConfig.class)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties({ Oauth2Properties.class })
@Import({ SecurityRedisAutoConfiguration.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class Oauth2ExtendAutoConfiguration {

	@ConditionalOnMissingBean(name = { "tokenStore" })
	@Bean("tokenStore")
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	/**
	 * 必须加入，不然自定义权限表达式不生效<br/>
	 * 
	 * 在 Oauth2Resource 中被public void configure(ResourceServerSecurityConfigurer
	 * resources)收集并配置
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

	/**
	 * 注入一个自定义token生成类
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnClass
	public TokenService tokenService() {
		return new SimpleTokenService();
	}

	/**
	 * 自定义token处理规则
	 * 
	 * @param tokenStore
	 * @param clientDetailsService
	 * @param accessTokenEnhancer
	 * @param authenticationManager
	 * @return
	 */
	@Bean("tokenStrategy")
	@ConditionalOnMissingBean(name = "tokenStrategy")
	@Primary
	public TokenStrategy tokenStrategy(TokenStore tokenStore, ClientDetailsService clientDetailsService,
			TokenEnhancer accessTokenEnhancer, TokenService tokenService, ApplicationContext context) {
		TokenStrategy tokenServices = new TokenStrategy(tokenStore, clientDetailsService, accessTokenEnhancer, context);
		tokenServices.setTokenService(tokenService);
		return tokenServices;
	}

	/**
	 * token生成工具
	 * 
	 * @param clientDetailsService
	 * @param authorizationServerTokenServices
	 * @param userDetailsService
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public TokenUtils tokenUtils(ClientDetailsService clientDetailsService,
			AuthorizationServerTokenServices authorizationServerTokenServices, TokenExtractor tokenExtractor,
			ConsumerTokenServices consumerTokenServices,@Qualifier("userDetailsService") UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		TokenUtils tokenUtils = new TokenUtils();
		tokenUtils.setClientDetailsService(clientDetailsService);
		tokenUtils.setUserDetailsService(userDetailsService);
		tokenUtils.setAuthorizationServerTokenServices(authorizationServerTokenServices);
		tokenUtils.setPasswordEncoder(passwordEncoder);
		tokenUtils.setConsumerTokenServices(consumerTokenServices);
		tokenUtils.setTokenExtractor(tokenExtractor);
		return tokenUtils;
	}

	/**
	 * 自定义异常转换器
	 * 
	 * @param exceptionProperties
	 * @return
	 */
	@Bean("auth2ResponseExceptionTranslator")
	@ConditionalOnMissingBean(name = "auth2ResponseExceptionTranslator")
	@SuppressWarnings("rawtypes")
	public WebResponseExceptionTranslator auth2ResponseExceptionTranslator(ErrorMsgUtil errorMsgUtil) {
		return new Auth2ResponseExceptionTranslator(errorMsgUtil);
	}

	/**
	 * 获取token时在BasicAuthenticationFilter之前增加一个过滤器
	 * 
	 * @return
	 */
	@Bean("tokenEndpointAuthenticationFilter")
	@ConditionalOnMissingBean(name = "tokenEndpointAuthenticationFilter")
	public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter(
			ClientDetailsService clientDetailsService, PasswordEncoder passwordEncoder, HandlerProcessor handlerProcessor,
			Oauth2Properties oauth2Properties) {
		TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter = new TokenEndpointAuthenticationFilter();
		tokenEndpointAuthenticationFilter.setClientDetailsService(clientDetailsService);
		tokenEndpointAuthenticationFilter.setPasswordEncoder(passwordEncoder);
		tokenEndpointAuthenticationFilter.setOauth2Properties(oauth2Properties);
		tokenEndpointAuthenticationFilter.setHandlerProcessor(handlerProcessor);
		return tokenEndpointAuthenticationFilter;
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

	/**
	 * Oauth2Server中用于异常转换
	 * 
	 * @param errorMsgUtil
	 * @return
	 */
	@Bean("authWebResponseExceptionTranslator")
	@ConditionalOnMissingBean(name = "authWebResponseExceptionTranslator")
	public WebResponseExceptionTranslator<OAuth2Exception> authWebResponseExceptionTranslator(
			ErrorMsgUtil errorMsgUtil) {
		AuthWebResponseExceptionTranslator authWebResponseExceptionTranslator = new AuthWebResponseExceptionTranslator();
		authWebResponseExceptionTranslator.setErrorMsgUtil(errorMsgUtil);
		return authWebResponseExceptionTranslator;
	}

	/**
	 * 配置一个过滤器，用于在oauth2中提前验证用户名和密码
	 * 
	 * @param userDetailsService
	 * @param passwordEncoder
	 * @return
	 */
	@Bean("tokenEndpointFilter")
	@ConditionalOnMissingBean(name = "tokenEndpointFilter")
	public Filter tokenEndpointFilter(@Qualifier("userDetailsService") UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,HandlerProcessor handlerProcessor) {
		TokenEndpointFilter tokenEndpointFilter = new TokenEndpointFilter();
		tokenEndpointFilter.setPasswordEncoder(passwordEncoder);
		tokenEndpointFilter.setUserDetailsService(userDetailsService);
		tokenEndpointFilter.setHandlerProcessor(handlerProcessor);
		return tokenEndpointFilter;
	}
	
	/**
	 * 将tokenEndpointFilter注入到security中
	 * @param tokenEndpointFilter
	 * @return
	 */
	@Bean("tokenEndpointInterceptor")
	@ConditionalOnMissingBean(name = "tokenEndpointInterceptor")
	public HttpSecurityInterceptor tokenEndpointInterceptor(@Qualifier("tokenEndpointFilter") Filter tokenEndpointFilter) {
		TokenEndpointInterceptor tokenEndpointInterceptor=new TokenEndpointInterceptor();
		tokenEndpointInterceptor.setFilter(tokenEndpointFilter);
		return tokenEndpointInterceptor;
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <Oauth2相关配置> 相关的配置");
	}

}
