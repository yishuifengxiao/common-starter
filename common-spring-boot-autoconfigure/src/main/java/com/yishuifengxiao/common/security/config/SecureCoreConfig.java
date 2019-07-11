/**
 * 
 */
package com.yishuifengxiao.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.encoder.impl.CustomPasswordEncoderImpl;
import com.yishuifengxiao.common.security.remerberme.CustomPersistentTokenRepository;
import com.yishuifengxiao.common.security.service.CustomeUserDetailsServiceImpl;
import com.yishuifengxiao.common.security.session.SessionInformationExpiredStrategyImpl;

/**
 * 安全配置<br/>
 * 通过@EnableConfigurationProperties 使自定义配置生效
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecureCoreConfig {
	/**
	 * 自定义用户查找
	 */
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 自定义属性配置
	 */
	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 注入自定义密码加密类
	 * 
	 * @return
	 */
	@Bean("passwordEncoder")
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new CustomPasswordEncoderImpl(securityProperties.getSecretKey());
	}

	/**
	 * 将密码加密类给注入进入
	 * 
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/**
	 * 注入用户查找配置类</br>
	 * 在系统没有注入UserDetailsService时，注册一个默认的UserDetailsService实例
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(UserDetailsService.class)
	public UserDetailsService userDetailsService() {
		return new CustomeUserDetailsServiceImpl(passwordEncoder());
	}

	/**
	 * 错误提示信息国际化
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		// messageSource.setBasenames("classpath:org/springframework/security/messages_zh_CN");
		messageSource.setBasenames("classpath*:com/yishuifengxiao/common/security/core/messages_zh_CN");
		// messageSource.setBasenames("classpath:messages_CN");
		return messageSource;
	}

	@Bean("persistentTokenRepository")
	@ConditionalOnMissingBean(PersistentTokenRepository.class)
	public PersistentTokenRepository persistentTokenRepository() {
		return new CustomPersistentTokenRepository();
	}

	@Bean
	@ConditionalOnMissingBean(SessionInformationExpiredStrategy.class)
	public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
		return new SessionInformationExpiredStrategyImpl();
	}

	@Bean
	public AcceptHeaderLocaleResolver acceptHeaderLocaleResolver() {
		return new AcceptHeaderLocaleResolver();
	}
}
