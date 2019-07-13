/**
 * 
 */
package com.yishuifengxiao.common.security.manager;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.yishuifengxiao.common.security.provider.AuthorizeConfigProvider;

/**
 * 收集系统中的所有授权配置默认实现
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public class DefaultAuthorizeConfigManager implements AuthorizeConfigManager {
	private final static Logger log = LoggerFactory.getLogger(DefaultAuthorizeConfigManager.class);
	/**
	 * 收集到所有的授权配置，Order的值越小，实例排在队列的越前面，这里需要使用有序队列
	 */
	private List<AuthorizeConfigProvider> authorizeConfigProviders;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		if (authorizeConfigProviders != null) {

			authorizeConfigProviders = authorizeConfigProviders.parallelStream().filter(t -> t != null)
					.sorted(Comparator.comparing(AuthorizeConfigProvider::getOrder)).collect(Collectors.toList());

			log.debug("==============================================> 系统中所有的授权提供器为 {}", authorizeConfigProviders);

			for (AuthorizeConfigProvider authorizeConfigProvider : authorizeConfigProviders) {
				log.debug("==============================================> 系统中当前加载的授权提供器序号为 {} , 实例为 {}",
						authorizeConfigProvider.getOrder(), authorizeConfigProvider);

				try {
					authorizeConfigProvider.config(config);
				} catch (Exception e) {
					log.error("===========================> 装载授权配置{}时出现问题，出现问题的原因为 {}",
							authorizeConfigProvider.getOrder(), e.getMessage());
				}

			}
			// 除了上面之外的所有的的配置，需要经过授权才能访问
			// 只要经过了授权就能访问,已经将此配置移动到InterceptAllAuthorizeConfigProvider
			// config.anyRequest().authenticated();
		}
	}

	public List<AuthorizeConfigProvider> getAuthorizeConfigProviders() {
		return authorizeConfigProviders;
	}

	public void setAuthorizeConfigProviders(List<AuthorizeConfigProvider> authorizeConfigProviders) {
		this.authorizeConfigProviders = authorizeConfigProviders;
	}

	public DefaultAuthorizeConfigManager(List<AuthorizeConfigProvider> authorizeConfigProviders) {
		this.authorizeConfigProviders = authorizeConfigProviders;
	}

	public DefaultAuthorizeConfigManager() {

	}

}
