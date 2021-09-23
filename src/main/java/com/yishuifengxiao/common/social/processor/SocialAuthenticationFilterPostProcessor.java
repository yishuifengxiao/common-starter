package com.yishuifengxiao.common.social.processor;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * spring social认证过滤器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SocialAuthenticationFilterPostProcessor {
	/**
	 * 认证处理
	 * 
	 * @param socialAuthenticationFilter 过滤器
	 */
	void process(SocialAuthenticationFilter socialAuthenticationFilter);
}