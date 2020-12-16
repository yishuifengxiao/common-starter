package com.yishuifengxiao.common.security.websecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * web安全授权器
 * 
 * @author qingteng
 * @date 2020年10月28日
 * @version 1.0.0
 */
public interface WebSecurityProvider {
	/**
	 * 配置WebSecurity 管理
	 * 
	 * @param propertyResource 资源管理器
	 * @param web
	 * @throws Exception
	 */
	void configure(PropertyResource propertyResource, WebSecurity web) throws Exception;
}
