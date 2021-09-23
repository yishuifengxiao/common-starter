package com.yishuifengxiao.common.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yishuifengxiao.common.social.processor.SocialAuthenticationFilterPostProcessor;

/**
 * 配置 spring social 过滤器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SsoSpringSocialConfigurer extends SpringSocialConfigurer {

	private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

	/**
	 * spring social 登陆时 需要拦截的url
	 */
	private String filterProcessesUrl;
	/**
	 * spring social 登陆成功后的跳转url
	 */
	private String singupUrl;

	public SsoSpringSocialConfigurer(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected <T> T postProcess(T object) {
		//重写qq登录url

		// spring social 登陆过程中一个重要的 过滤器
		SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
		filter.setFilterProcessesUrl(filterProcessesUrl);
		filter.setSignupUrl(singupUrl);

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

	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	public String getSingupUrl() {
		return singupUrl;
	}

	public void setSingupUrl(String singupUrl) {
		this.singupUrl = singupUrl;
	}

	public SsoSpringSocialConfigurer(SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor,
			String filterProcessesUrl, String singupUrl) {
		this.socialAuthenticationFilterPostProcessor = socialAuthenticationFilterPostProcessor;
		this.filterProcessesUrl = filterProcessesUrl;
		this.singupUrl = singupUrl;
	}

	public SsoSpringSocialConfigurer() {

	}

}