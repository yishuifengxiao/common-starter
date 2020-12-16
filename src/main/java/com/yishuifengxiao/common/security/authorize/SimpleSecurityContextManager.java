package com.yishuifengxiao.common.security.authorize;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

import com.yishuifengxiao.common.security.httpsecurity.HttpSecurityInterceptor;
import com.yishuifengxiao.common.security.provider.AuthorizeProvider;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.websecurity.WebSecurityProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统安全管理器
 * 
 * @author yishui
 * @date 2019年10月12日
 * @version 1.0.0
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

	@Override
	public void config(HttpSecurity http) throws Exception {
		// 将HttpSecurityInterceptor 实例装载到security中
		if (null != this.interceptors) {
			for (HttpSecurityInterceptor interceptor : this.interceptors) {
				http.apply(interceptor);
			}
		}
		// 加入自定义的授权配置
		if (null != this.authorizeConfigProviders) {

			this.authorizeConfigProviders = this.authorizeConfigProviders.parallelStream().filter(Objects::nonNull)
					.sorted(Comparator.comparing(AuthorizeProvider::getOrder)).collect(Collectors.toList());

			log.debug("【易水组件】 系统中所有的授权提供器为 {}", authorizeConfigProviders);

			for (AuthorizeProvider authorizeConfigProvider : authorizeConfigProviders) {
				log.debug("【易水组件】 系统中当前加载的授权提供器序号为 {} , 实例为 {}", authorizeConfigProvider.getOrder(),
						authorizeConfigProvider);

				try {
					authorizeConfigProvider.config(propertyResource, http.authorizeRequests());
				} catch (Exception e) {
					log.error("【易水组件】 装载授权配置{}时出现问题，出现问题的原因为 {}", authorizeConfigProvider.getOrder(), e.getMessage());
				}

			}

		}

	}

	/**
	 * 配置WebSecurity
	 * 
	 * @param web
	 * @throws Exception
	 */
	@Override
	public void config(WebSecurity web) throws Exception {

		if (null != this.webSecurityProviders) {
			for (WebSecurityProvider webSecurityProvider : webSecurityProviders) {
				webSecurityProvider.configure(propertyResource, web);
			}
		}

	}

	public List<HttpSecurityInterceptor> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<HttpSecurityInterceptor> interceptors) {
		this.interceptors = interceptors;
	}

	public List<AuthorizeProvider> getAuthorizeConfigProviders() {
		return authorizeConfigProviders;
	}

	public void setAuthorizeConfigProviders(List<AuthorizeProvider> authorizeConfigProviders) {
		this.authorizeConfigProviders = authorizeConfigProviders;
	}

	public List<WebSecurityProvider> getWebSecurityProviders() {
		return webSecurityProviders;
	}

	public void setWebSecurityProviders(List<WebSecurityProvider> webSecurityProviders) {
		this.webSecurityProviders = webSecurityProviders;
	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}



}
