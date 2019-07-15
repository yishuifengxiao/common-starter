package com.yishuifengxiao.common.security.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yishuifengxiao.common.security.social.processor.SocialAuthenticationFilterPostProcessor;

/**
 * 配置 spring social 过滤器
 * 
 * @author yishui
 * @date 2019年7月15日
 * @version 1.0.0
 */
public class SsoSpringSocialConfigurer extends SpringSocialConfigurer {

	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	// 设置自定义url
	private String filterProcessesUrl;

	public SsoSpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	/**
	 * 重写qq登录url
	 *
	 * @param object
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {

		// spring social 登陆过程中一个重要的 过滤器
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);

		// 给 SocialAuthenticationFilter 添加一个成功处理器
		if (socialAuthenticationFilterPostProcessor != null) {
			socialAuthenticationFilterPostProcessor.process(filter);
		}

		// 返回spring social 过滤器
		return (T) filter;
	}

	public SocialAuthenticationFilterPostProcessor getSocialAuthenticationFilterPostProcessor() {
		return socialAuthenticationFilterPostProcessor;
	}

	public void setSocialAuthenticationFilterPostProcessor(
			SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor) {
		this.socialAuthenticationFilterPostProcessor = socialAuthenticationFilterPostProcessor;
	}
}