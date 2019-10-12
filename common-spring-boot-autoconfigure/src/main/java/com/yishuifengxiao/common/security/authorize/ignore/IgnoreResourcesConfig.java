package com.yishuifengxiao.common.security.authorize.ignore;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * 忽视资源配置管理
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public interface IgnoreResourcesConfig {
	/**
	 * 配置需要忽视的资源
	 * 
	 * @param web
	 * @throws Exception
	 */
	void configure(WebSecurity web) throws Exception;
}
