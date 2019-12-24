package com.yishuifengxiao.common.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.security.authorize.intercept.AuthorizeResourceProvider;
import com.yishuifengxiao.common.security.manager.authorize.AuthorizeConfigManager;
import com.yishuifengxiao.common.security.oauth2.token.TokenStrategy;

/**
 * oauth2 资源相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class Oauth2Resource extends ResourceServerConfigurerAdapter {

	@Autowired
	private Oauth2Properties oauth2Properties;

	/**
	 * 定义在security-core包中
	 */
	@Autowired
	private AccessDeniedHandler customAccessDeniedHandler;

	@Autowired
	private AuthorizeConfigManager authorizeConfigManager;

	@Autowired
	private AuthorizeResourceProvider resourceAuthorityProvider;
	/**
	 * 定义在security-core包中
	 */
	@Autowired
	@Qualifier("exceptionAuthenticationEntryPoint")
	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	@Autowired
	private DefaultWebSecurityExpressionHandler expressionHandler;

	@Autowired
	@Qualifier("tokenExtractor")
	private TokenExtractor tokenExtractor;

	@SuppressWarnings("rawtypes")
	@Autowired
	@Qualifier("auth2ResponseExceptionTranslator")
	private WebResponseExceptionTranslator auth2ResponseExceptionTranslator;

	/**
	 * token生成器，负责token的生成或获取
	 */
	@Autowired
	@Qualifier("tokenStrategy")
	private TokenStrategy tokenStrategy;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {

		// 定义异常转换类生效
		AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint)
				.setExceptionTranslator(auth2ResponseExceptionTranslator);

		resources.authenticationEntryPoint(authenticationEntryPoint);
		// 自定义token信息提取器
		tokenExtractor = tokenExtractor == null ? new BearerTokenExtractor() : tokenExtractor;
		resources.tokenExtractor(tokenExtractor);
		// 权限拒绝处理器
		resources.accessDeniedHandler(customAccessDeniedHandler);
		resources.stateless(false);
		// 不然自定义权限表达式不生效
		resources.expressionHandler(expressionHandler);
		resources.resourceId(this.oauth2Properties.getRealm());
		// token的验证和读取策略
		resources.tokenServices(tokenStrategy);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//@formatter:off  


//		// 开启http baisc认证
//		if (securityProperties.getHttpBasic()) {
//			http.httpBasic() // 开启basic认证
//			        .authenticationEntryPoint(exceptionAuthenticationEntryPoint)
//					.realmName(securityProperties.getRealmName());
//		}
//		

		//决定哪些资源需要经过授权管理
		resourceAuthorityProvider.configure(http);

		
		//自定义授权配置
		authorizeConfigManager.config(http.authorizeRequests());
		
		
//		RequestMatcherConfigurer  requestMatcherConfigurer=http.requestMatchers();
//
//		http.requestMatcher(requestMatcher)
	//	requestMatcherConfigurer.anyRequest()
			//.anyRequest();
		

//		
//        //具体的授权表达式
//		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry=http.authorizeRequests();
//		
//
//		
//		//直接放行的路径
//		expressionInterceptUrlRegistry.antMatchers(
//						securityProperties.getSession().getSessionInvalidUrl(),//session失效时的url
//						securityProperties.getCore().getRedirectUrl() // 权限拦截时默认的跳转地址
//						)
//					.permitAll();
//
//
//		
//		//自定义授权表达式的路径
//		if(securityProperties.getCustom().getAll()!=null) {
//			for(String path:securityProperties.getCustom().getAll()) {
//				expressionInterceptUrlRegistry
//					.antMatchers(path)
//					.access("@customAuthority.hasPermission(request, authentication)");
//			}
//			}
//		
//		
//		//其余的路径登录后才能访问
//		expressionInterceptUrlRegistry.anyRequest()
//		            .authenticated();
		

		
//		http
//			.exceptionHandling()
//			.authenticationEntryPoint(exceptionAuthenticationEntryPoint)// 定义的不存在access_token时候响应
//			.accessDeniedHandler(customAccessDeniedHandler)//自定义权限拒绝处理器
//			;
		//@formatter:on  

	}

}
