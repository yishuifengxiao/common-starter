package com.yishuifengxiao.common.security.websecurity;

import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.resource.PropertyResource;

/**
 * web安全授权器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface WebSecurityProvider {
	/**
	 * 配置WebSecurity 管理
	 * 
	 * @param propertyResource 资源管理器
	 * @param web              WebSecurity
	 * @throws Exception 处理时发生了问题
	 */
	void configure(PropertyResource propertyResource, WebSecurity web) throws Exception;
}
