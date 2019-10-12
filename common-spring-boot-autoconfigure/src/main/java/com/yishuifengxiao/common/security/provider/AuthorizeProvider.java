/**
 * 
 */
package com.yishuifengxiao.common.security.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * spring security 授权提供器
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public interface AuthorizeProvider {
	/**
	 * 授权配置
	 * 
	 * @param config
	 * @throws Exception 
	 */
	void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) throws Exception;

	/**
	 * 授权提供器的顺序，数字越小越是提前使用，默认值为100
	 * 
	 * @return
	 */
	int getOrder();

}
