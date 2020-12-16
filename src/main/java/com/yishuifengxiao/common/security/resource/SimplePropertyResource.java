/**
 * 
 */
package com.yishuifengxiao.common.security.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.social.SocialProperties;

/**
 * 简单实现的资源管理器
 * 
 * @author qingteng
 * @date 2020年11月27日
 * @version 1.0.0
 */
public class SimplePropertyResource implements PropertyResource {

	/**
	 * 系统默认包含的静态路径
	 */
	private static String[] STATIC_RESOURCE = new String[] { "/js/**", "/css/**", "/images/**", "/fonts/**",
			"/**/**.png", "/**/**.jpg", "/**/**.html", "/**/**.ico", "/**/**.js", "/**/**.css", "/**/**.woff",
			"/**/**.ttf" };

	/**
	 * 系统默认包含的swagger-ui资源路径
	 */
	private static String[] SWAGGER_UI_RESOURCE = new String[] { "/swagger-ui.html", "/swagger-resources/**",
			"/v2/api-docs", "/swagger-ui/**", "/v3/**" };
	/**
	 * 系统默认包含actuator相关的路径
	 */
	private static String[] ACTUATOR_RESOURCE = new String[] { "/actuator/**" };
	/**
	 * 系统默认包含webjars相关的路径
	 */
	private static String[] WEBJARS_RESOURCE = new String[] { "/webjars/**" };
	/**
	 * 所有的资源
	 */
	private static String[] ALL_RESOURCE = new String[] { "/**" };

	/**
	 * spring security 属性配置文件
	 */
	private SecurityProperties securityProperties;
	/**
	 * spring social 属性配置文件
	 */
	private SocialProperties socialProperties;

	@Override
	public SecurityProperties security() {
		return this.securityProperties;
	}

	@Override
	public SocialProperties social() {
		return this.socialProperties;
	}

	@Override
	public Set<String> getAllPermitUlrs() {
		// 获取配置的资源
		Set<String> urls = this.getUrls(this.securityProperties.getPermits());
		// 需要增加的资源
		urls.addAll(Arrays.asList(
				// 获取token的地址
				OAuth2Constant.OAUTH_TOKEN,
				// 校验token的地址
				OAuth2Constant.OAUTH_CHECK_TOKEN,
				// 权限拦截时默认的跳转地址
				securityProperties.getCore().getRedirectUrl(),
				// 登陆页面的URL
				securityProperties.getCore().getLoginPage(),
				// session失效时跳转的地址
				securityProperties.getSession().getSessionInvalidUrl()

		));

		return urls;
	}

	@Override
	public List<String> getExcludeUrls() {
		Set<String> urls = this.getUrls(this.securityProperties.getExcludes());
		urls.addAll(Arrays.asList(
				// QQ登陆的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getQq().getProviderId(),
				// 微信登陆的地址
				socialProperties.getFilterProcessesUrl() + "/" + socialProperties.getWeixin().getProviderId(),
				// qq登陆成功后跳转的地址
				socialProperties.getQq().getRegisterUrl(),
				// 微信登陆成功后跳转的地址
				socialProperties.getWeixin().getRegisterUrl()));

		return urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
	}

	@Override
	public Set<String> getAllCustomUrls() {
		return this.getUrls(this.securityProperties.getCustoms());
	}

	@Override
	public Set<String> getAllUnCheckUrls() {
		Set<String> urls = this.getUrls(this.securityProperties.getUnchecks());
		// 所有直接放行的资源
		urls.addAll(this.getAllPermitUlrs());
		// 所有忽视的资源
		urls.addAll(Arrays.asList(this.getAllIgnoreUrls()));
		//登陆地址
		urls.add(this.securityProperties.getCore().getFormActionUrl());
		// 短信登陆地址
		urls.add(this.securityProperties.getCode().getSmsLoginUrl());
		return urls.stream().filter(StringUtils::isNotBlank).collect(Collectors.toSet());
	}

	@Override
	public String[] getAllIgnoreUrls() {
		Set<String> set = new HashSet<>();
		if (this.securityProperties.getIgnore().getContainStaticResource()) {
			set.addAll(Arrays.asList(STATIC_RESOURCE));
		}
		if (this.securityProperties.getIgnore().getContainSwaagerUiResource()) {
			set.addAll(Arrays.asList(SWAGGER_UI_RESOURCE));
		}
		if (this.securityProperties.getIgnore().getContainActuator()) {
			set.addAll(Arrays.asList(ACTUATOR_RESOURCE));
		}
		if (this.securityProperties.getIgnore().getContainWebjars()) {
			set.addAll(Arrays.asList(WEBJARS_RESOURCE));
		}
		if (this.securityProperties.getIgnore().getContainAll()) {
			set.addAll(Arrays.asList(ALL_RESOURCE));
		}
		set.addAll(this.getUrls(this.securityProperties.getIgnore().getUrls()));
		return set.toArray(new String[] {});
	}

	/**
	 * 提取出Map里存储的URL
	 * 
	 * @param map 存储资源路径的map
	 * @return 所有过滤后的资源路径
	 */
	private Set<String> getUrls(Map<String, String> map) {
		Set<String> urls = new HashSet<>();
		if (null != map) {
			map.forEach((k, v) -> {
				if (StringUtils.isNoneBlank(k, v)) {
					urls.addAll(Arrays.asList(v.split(",")).parallelStream().filter(StringUtils::isNotBlank)
							.map(t -> t.trim()).collect(Collectors.toList()));
				}
			});
		}
		return urls;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public void setSocialProperties(SocialProperties socialProperties) {
		this.socialProperties = socialProperties;
	}

}
