/**
 * 
 */
package com.yishuifengxiao.common.security.security.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;

import com.yishuifengxiao.common.security.security.provider.AuthorizeConfigProvider;

/**
 * 收集系统中的所有授权配置默认实现
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
@Component
public class DefaultAuthorizeConfigManager implements AuthorizeConfigManager {
	private final static Logger log = LoggerFactory.getLogger(DefaultAuthorizeConfigManager.class);
	/**
	 * 收集到所有的授权配置，Order的值越小，实例排在队列的越前面，这里需要使用有序队列
	 */
	@Autowired
	private List<AuthorizeConfigProvider> authorizeConfigProviders;

	@Override
	public void config(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config) {
		if (authorizeConfigProviders != null) {
			authorizeConfigProviders.parallelStream().filter(t -> t != null).sorted((p1, p2) -> {
				return p1.getOrder() - p2.getOrder();
			}).collect(Collectors.toList()).forEach(authorizeConfigProvider -> {
				log.debug("==============================================> 当前装配的 授权配置的顺序为 {}, 具体信息为 {}",
						authorizeConfigProvider.getOrder(), authorizeConfigProvider);
				try {
					authorizeConfigProvider.config(config);
				} catch (Exception e) {
					log.error("===========================> 装载授权配置{}时出现问题，出现问题的原因为 {}",
							authorizeConfigProvider.getOrder(), e.getMessage());
				}
			});
			// 除了上面之外的所有的的配置，需要经过授权才能访问
			// 只要经过了授权就能访问,已经将此配置移动到InterceptAllAuthorizeConfigProvider
			//config.anyRequest().authenticated();

		}
	}

}
