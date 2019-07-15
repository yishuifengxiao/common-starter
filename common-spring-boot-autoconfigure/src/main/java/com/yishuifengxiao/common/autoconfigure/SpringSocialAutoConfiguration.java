package com.yishuifengxiao.common.autoconfigure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.social.SsoSpringSocialConfigurer;
import com.yishuifengxiao.common.security.social.adapter.SocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.processor.SocialAuthenticationFilterPostProcessor;
import com.yishuifengxiao.common.security.social.processor.impl.SsoSocialAuthenticationFilterPostProcessor;
import com.yishuifengxiao.common.security.social.qq.QQSocialAutoConfigurerAdapter;

@Configuration
@EnableConfigurationProperties(SocialProperties.class)
public class SpringSocialAutoConfiguration extends SocialConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Bean
	public SocialAutoConfigurerAdapter qQSocialAutoConfigurerAdapter(SocialProperties socialProperties) {

		QQSocialAutoConfigurerAdapter qQSocialAutoConfigurerAdapter = new QQSocialAutoConfigurerAdapter();
		qQSocialAutoConfigurerAdapter.setQqAppId(socialProperties.getQq().getAppId());
		qQSocialAutoConfigurerAdapter.setQqAppSecret(socialProperties.getQq().getAppSecret());
		qQSocialAutoConfigurerAdapter.setQqProviderId(socialProperties.getQq().getProviderId());
		return qQSocialAutoConfigurerAdapter;
	}

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		// 设置表名前缀
		// usersConnectionRepository.setTablePrefix("dsdsf");
		return usersConnectionRepository;
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	@Bean
	public SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor(
			AuthenticationSuccessHandler jsAuthenticationSuccessHandler) {
		SsoSocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor = new SsoSocialAuthenticationFilterPostProcessor();
		socialAuthenticationFilterPostProcessor.setJsAuthenticationSuccessHandler(jsAuthenticationSuccessHandler);
		return socialAuthenticationFilterPostProcessor;
	}

	/**
	 * 自定义qq登录路径和注册路径
	 *
	 * @return
	 */
	@Bean
	@Autowired
	public SpringSocialConfigurer ssbSocialSecurityConfig(
			SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor,
			SocialProperties socialProperties) {
		SsoSpringSocialConfigurer configurer = new SsoSpringSocialConfigurer(
				socialProperties.getQq().getFilterProcessesUrl());
		// 1、认证失败跳转注册页面
		// 跳转到signUp controller，从session中获取用户信息并通过生成的uuid保存到redis里面，然后跳转bind页面
		// 前端绑定后发送用户信息到后台bind controller，1）保存到自己系统用户；2）保存一份userconnection表数据，Spring
		// Social通过这里面表数据进行判断是否绑定
		configurer.signupUrl(socialProperties.getQq().getRegisterUrl());
		// 2、认证成功跳转后处理器，跳转带token的成功页面
		configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}

}
