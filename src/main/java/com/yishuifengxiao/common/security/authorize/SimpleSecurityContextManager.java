package com.yishuifengxiao.common.security.authorize;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统安全管理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleSecurityContextManager implements SecurityContextManager {

	/**
	 * 收集到所有的授权配置，Order的值越小，实例排在队列的越前面，这里需要使用有序队列
	 */
	private List<AuthorizeProvider> authorizeConfigProviders;

	private List<HttpSecurityInterceptor> interceptors;

	/**
	 * web安全授权器实例
	 */
	protected List<WebSecurityProvider> webSecurityProviders;

	/**
	 * 资源路径器
	 */
	private PropertyResource propertyResource;

	/**
	 * 系统中所有的Security 请求过滤器 实例
	 */
	private List<SecurityRequestFilter> securityRequestFilters;

	/**
	 * 是否显示加载日志
	 */
	private boolean show = false;

	@Override
	public void config(HttpSecurity http) throws Exception {

		if (null != this.securityRequestFilters) {
			for (SecurityRequestFilter securityRequestFilter : this.securityRequestFilters) {
				if (this.show) {
					log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( Security请求过滤器 ) 实例为 {}", securityRequestFilter);
				}

				securityRequestFilter.configure(http);
			}
		}

		// 将HttpSecurityInterceptor 实例装载到security中
		if (null != this.interceptors) {
			for (HttpSecurityInterceptor interceptor : this.interceptors) {
				if (this.show) {
					log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( 资源授权拦截器 ) 实例为 {}", interceptor);
				}

				http.apply(interceptor);
			}
		}
		// 加入自定义的授权配置
		if (null != this.authorizeConfigProviders) {

			this.authorizeConfigProviders = this.authorizeConfigProviders.parallelStream().filter(Objects::nonNull)
					.sorted(Comparator.comparing(AuthorizeProvider::getOrder)).collect(Collectors.toList());

			for (AuthorizeProvider authorizeConfigProvider : authorizeConfigProviders) {
				if (this.show) {
					log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( 授权提供器 ) 序号为 {} , 实例为 {}", authorizeConfigProvider.getOrder(),
							authorizeConfigProvider);
				}

				authorizeConfigProvider.config(propertyResource, http.authorizeRequests());

			}

		}

	}

	@Override
	public void config(WebSecurity web) throws Exception {

		if (null != this.webSecurityProviders) {
			for (WebSecurityProvider webSecurityProvider : webSecurityProviders) {
				if (this.show) {
					log.info("【yishuifengxiao-common-spring-boot-starter】 系统中当前加载的 ( web安全授权器 ) 实例为 {}", webSecurityProvider);
				}
				webSecurityProvider.configure(propertyResource, web);
			}
		}

	}

	public SimpleSecurityContextManager(List<AuthorizeProvider> authorizeConfigProviders,
			List<HttpSecurityInterceptor> interceptors, List<WebSecurityProvider> webSecurityProviders,
			PropertyResource propertyResource, List<SecurityRequestFilter> securityRequestFilters) {

		this.authorizeConfigProviders = authorizeConfigProviders;
		this.interceptors = interceptors;
		this.webSecurityProviders = webSecurityProviders;
		this.propertyResource = propertyResource;
		this.securityRequestFilters = securityRequestFilters;
		// 是否显示加载日志
		this.show = BooleanUtils.isTrue(propertyResource.security().getShowDeatil());
	}

}
