package com.yishuifengxiao.common.oauth2;

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

import com.yishuifengxiao.common.oauth2.provider.TokenStrategy;
import com.yishuifengxiao.common.security.authorize.SecurityContextManager;

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

	/**
	 * 安全授权配置管理器
	 */
	@Autowired
	protected SecurityContextManager securityContextManager;

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
		
		//自定义授权配置
		securityContextManager.config(http);


		
		//@formatter:on  

	}

}
