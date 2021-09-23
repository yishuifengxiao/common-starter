package com.yishuifengxiao.common.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * <p>核心的spring security配置 </p>
 * <p>此配置为示例配置，在使用的项目中，需要进入如下配置</p>
 * 并在类接口上配置 注解
 * 
 * <pre>
 *  &#64;EnableWebSecurity
 * </pre>
 * 
 * 开启spring social时，需要先注入SpringSocialConfigurer
 * 
 * <pre>
 * &#64;Autowired(required = false)
 * private SpringSocialConfigurer socialSecurityConfig;
 * </pre>
 * 
 * 接着在 configure 方法中加入以下配置
 * 
 * <pre>
 * http.apply(socialSecurityConfig);
 * </pre>
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecurityConfig extends AbstractSecurityConfig {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 调用父类中的默认配置
		super.configure(http);

	}

}
