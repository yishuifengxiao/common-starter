/**
 * 
 */
package com.yishuifengxiao.common.security.security.manager;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

/**
 * 收集系统中的所有授权配置
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1 
 */
public interface AuthorizeConfigManager {
	/**
	 * 收集系统中的所有授权配
	 * 
	 * @param config
	 */
	void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config);
}
