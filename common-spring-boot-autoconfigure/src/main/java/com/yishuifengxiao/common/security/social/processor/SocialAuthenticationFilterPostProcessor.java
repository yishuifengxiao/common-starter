package com.yishuifengxiao.common.security.social.processor;

import org.springframework.social.security.SocialAuthenticationFilter;
/**
 * spring social认证过滤器
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public interface SocialAuthenticationFilterPostProcessor {
	/**
	 * 
	 * @param socialAuthenticationFilter
	 */
	void process(SocialAuthenticationFilter socialAuthenticationFilter);
}