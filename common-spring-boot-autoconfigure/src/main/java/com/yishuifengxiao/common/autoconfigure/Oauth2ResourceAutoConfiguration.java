package com.yishuifengxiao.common.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity.RequestMatcherConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import com.yishuifengxiao.common.properties.Oauth2Properties;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.oauth2.translator.Auth2ResponseExceptionTranslator;

@Configuration
@ConditionalOnClass({ EnableResourceServer.class })
@ConditionalOnWebApplication
@ConditionalOnBean(ResourceServerConfiguration.class)
@AutoConfigureAfter({ SecurityAutoConfiguration.class, SecurityAutoConfiguration.class })
@EnableConfigurationProperties(Oauth2Properties.class)
public class Oauth2ResourceAutoConfiguration extends ResourceServerConfigurerAdapter {

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

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {

		// 定义异常转换类生效
		AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
		((OAuth2AuthenticationEntryPoint) authenticationEntryPoint)
				.setExceptionTranslator(new Auth2ResponseExceptionTranslator());
		resources.authenticationEntryPoint(authenticationEntryPoint);
		resources.resourceId(this.oauth2Properties.getRealm()).expressionHandler(expressionHandler);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		//@formatter:off  
		
		
		// 注入所有的授权适配器
	   
		//需要需要经过授权管理的资源的路径,所有的资源都要经过授权管理
		RequestMatcherConfigurer requestMatcherConfigurer=http.requestMatchers();
		requestMatcherConfigurer.antMatchers("/**");
		
		//具体的授权规则
		
		//下面是直接放过通行的路径
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry=http.authorizeRequests();
		expressionInterceptUrlRegistry.antMatchers(
						"/oauth/token", 
						securityProperties.getCore().getRedirectUrl(), // 权限拦截时默认的跳转地址
						securityProperties.getCore().getLoginPage(), // 登陆页面的URL
						securityProperties.getCore().getFormActionUrl(), // 登陆页面表单提交地址
						securityProperties.getCore().getLoginOutUrl(),//退出页面
						securityProperties.getSession().getSessionInvalidUrl() //session失效时跳转的页面
						).permitAll();
		
		
		//其余的路径都需要经过认证才能访问
		expressionInterceptUrlRegistry.anyRequest().authenticated();
		http
			.exceptionHandling()
			.authenticationEntryPoint(exceptionAuthenticationEntryPoint)// 定义的不存在access_token时候响应
			.accessDeniedHandler(customAccessDeniedHandler)//自定义权限拒绝处理器
			;
		//@formatter:on  
	}

}
