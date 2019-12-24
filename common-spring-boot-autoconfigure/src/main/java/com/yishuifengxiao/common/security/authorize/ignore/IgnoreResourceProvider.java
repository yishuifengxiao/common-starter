package com.yishuifengxiao.common.security.authorize.ignore;

import org.springframework.security.config.annotation.web.builders.WebSecurity.IgnoredRequestConfigurer;

/**
 * 忽视资源配置管理
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
 */
public interface IgnoreResourceProvider {
	/**
	 * 配置需要忽视的资源
	 * 
	 * @param ignoring
	 * @throws Exception
	 */
	void configure(IgnoredRequestConfigurer ignoring) throws Exception;
}
