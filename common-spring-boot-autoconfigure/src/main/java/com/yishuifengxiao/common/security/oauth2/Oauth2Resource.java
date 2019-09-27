package com.yishuifengxiao.common.security.oauth2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.matcher.ExcludeRequestMatcher;
import com.yishuifengxiao.common.security.oauth2.translator.Auth2ResponseExceptionTranslator;

public class Oauth2Resource extends ResourceServerConfigurerAdapter {

	@Autowired
	private Oauth2Properties oauth2Properties;

	/**
	 * 定义在security-core包中
	 */
	@Autowired
	private AccessDeniedHandler customAccessDeniedHandler;

	/**
	 * 定义在security-core包中
	 */
	@Autowired
	@Qualifier("exceptionAuthenticationEntryPoint")
	private AuthenticationEntryPoint exceptionAuthenticationEntryPoint;

	@Autowired
	private SecurityProperties securityProperties;

	@Autowired
	private SocialProperties socialProperties;

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

	@Autowired
	private DefaultWebSecurityExpressionHandler expressionHandler;
	
	@Autowired
	@Qualifier("tokenExtractor")
	private TokenExtractor tokenExtractor;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {

		// 定义异常转换类生效
		AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint)
				.setExceptionTranslator(new Auth2ResponseExceptionTranslator());
		resources.authenticationEntryPoint(authenticationEntryPoint);
		tokenExtractor=tokenExtractor==null?new BearerTokenExtractor(): tokenExtractor;
		resources.tokenExtractor(tokenExtractor);
		resources.resourceId(this.oauth2Properties.getRealm()).expressionHandler(expressionHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//@formatter:off  
		http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
		
		// 注入所有的授权适配器


		//所有的路径都要经过授权
		http.requestMatcher(new ExcludeRequestMatcher(getExcludeUrls()));
		
//		RequestMatcherConfigurer  requestMatcherConfigurer=http.requestMatchers();
//
//		http.requestMatcher(requestMatcher)
	//	requestMatcherConfigurer.anyRequest()
			//.anyRequest();
		

		
        //具体的授权表达式
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry=http.authorizeRequests();
		

		
		//直接放行的路径
		expressionInterceptUrlRegistry.antMatchers(
						securityProperties.getSession().getSessionInvalidUrl(),//session失效时的url
						securityProperties.getCore().getRedirectUrl() // 权限拦截时默认的跳转地址
						)
					.permitAll();


		
		//自定义授权表达式的路径
		if(securityProperties.getCustom().getAll()!=null) {
			for(String path:securityProperties.getCustom().getAll()) {
				expressionInterceptUrlRegistry
					.antMatchers(path)
					.access("@customAuthority.hasPermission(request, authentication)");
			}
			}
		
		
		//其余的路径登录后才能访问
		expressionInterceptUrlRegistry.anyRequest()
		            .authenticated();
		


		
		http
			.exceptionHandling()
			.authenticationEntryPoint(exceptionAuthenticationEntryPoint)// 定义的不存在access_token时候响应
			.accessDeniedHandler(customAccessDeniedHandler)//自定义权限拒绝处理器
			;
		//@formatter:on  
	}

	/**
	 * 获取所有不经过oauth2管理的路径
	 * 
	 * @return
	 */
	private List<String> getExcludeUrls() {
		List<String> excludeUrls = Arrays.asList("/oauth/**",
				securityProperties.getHandler().getSuc().getRedirectUrl(),//登录成功后跳转的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getQq().getProviderId(), // QQ登陆的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getWeixin().getProviderId(), // 微信登陆的地址
				socialProperties.getQq().getRegisterUrl(),//qq登陆成功后跳转的地址
				socialProperties.getWeixin().getRegisterUrl(),//微信登陆成功后跳转的地址
				securityProperties.getCore().getLoginPage(), // 登陆页面的URL
				securityProperties.getCore().getFormActionUrl(), // 登陆页面表单提交地址
				securityProperties.getCore().getLoginOutUrl() // 退出页面
		);
		excludeUrls.addAll(oauth2Properties.getExcludeUrls());
		return excludeUrls.stream().distinct().collect(Collectors.toList());
	}

}
