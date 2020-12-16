/**
 * 
 */
package com.yishuifengxiao.common.security.provider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * <strong>授权提供器</strong><br/>
 * <br/>
 * 对系统进行授权配置操作
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public interface AuthorizeProvider {

	/**
	 * 授权配置
	 * 
	 * @param propertyResource                    授权资源
	 * @param expressionInterceptUrlRegistry 注册器
	 * @throws Exception
	 */
	void config(PropertyResource propertyResource,
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry)
			throws Exception;

	/**
	 * 授权提供器的顺序，数字越小越是提前使用，默认值为100
	 * 
	 * @return
	 */
	int getOrder();

}
