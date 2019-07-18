package com.yishuifengxiao.common.autoconfigure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import com.yishuifengxiao.common.properties.SocialProperties;
import com.yishuifengxiao.common.security.social.SsoSpringSocialConfigurer;
import com.yishuifengxiao.common.security.social.adapter.SocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.processor.SocialAuthenticationFilterPostProcessor;
import com.yishuifengxiao.common.security.social.processor.impl.SsoSocialAuthenticationFilterPostProcessor;
import com.yishuifengxiao.common.security.social.qq.QQSocialAutoConfigurerAdapter;
import com.yishuifengxiao.common.security.social.weixin.WechatAutoConfigurerAdapter;

@Configuration
@EnableConfigurationProperties(SocialProperties.class)
@ConditionalOnClass({ ConnectController.class, SocialConfigurerAdapter.class })
@ConditionalOnBean({ ConnectionFactoryLocator.class, UsersConnectionRepository.class })
public class SpringSocialAutoConfiguration extends SocialConfigurerAdapter {

	@Autowired(required = false)
	private DataSource dataSource;

	@Autowired(required = false)
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired(required = false)
	private ConnectionSignUp connectionSignUp;

	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
		if (usersConnectionRepository != null) {
			return usersConnectionRepository;
		}

		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource,
				connectionFactoryLocator, Encryptors.noOpText());
		// 可以配置 ConnectionSignUp 以跳过注册流程
		if (connectionSignUp != null) {
			usersConnectionRepository.setConnectionSignUp(connectionSignUp);
		}

		// 设置表名前缀
		// usersConnectionRepository.setTablePrefix("dsdsf");
		return usersConnectionRepository;
	}

	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	/**
	 * QQ登陆连接工厂
	 * 
	 * @param socialProperties
	 * @return
	 */
	@Bean("qqSocialAutoConfigurerAdapter")
	public SocialAutoConfigurerAdapter qqSocialAutoConfigurerAdapter(SocialProperties socialProperties) {

		QQSocialAutoConfigurerAdapter qqSocialAutoConfigurerAdapter = new QQSocialAutoConfigurerAdapter();
		qqSocialAutoConfigurerAdapter.setQqAppId(socialProperties.getQq().getAppId());
		qqSocialAutoConfigurerAdapter.setQqAppSecret(socialProperties.getQq().getAppSecret());
		qqSocialAutoConfigurerAdapter.setQqProviderId(socialProperties.getQq().getProviderId());
		return qqSocialAutoConfigurerAdapter;
	}

	/**
	 * 用于在回调时获取到信息<br/>
	 * 
	 * 【作用】在注册过程中获取到spring social的用户信息 <br/>
	 * 
	 * 通过以下方法 实现 个人用户 和spring social用户 关系绑定 <br/>
	 * providerSignInUtils.doPostSignUp(个人用户 id , new ServletWebRequest(request,
	 * response));
	 * 
	 * @param connectionFactoryLocator
	 * @return
	 */
	@Bean
	public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
		return new ProviderSignInUtils(connectionFactoryLocator,
				getUsersConnectionRepository(connectionFactoryLocator));
	}

	/**
	 * spring social QQ处理器
	 * 
	 * @param jsAuthenticationSuccessHandler
	 * @return
	 */
	@Bean
	public SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor(
			AuthenticationSuccessHandler jsAuthenticationSuccessHandler) {
		SsoSocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor = new SsoSocialAuthenticationFilterPostProcessor();
		socialAuthenticationFilterPostProcessor.setJsAuthenticationSuccessHandler(jsAuthenticationSuccessHandler);
		return socialAuthenticationFilterPostProcessor;
	}

	/**
	 * 自定义qq登录路径和注册路径【此服务需要注册spring security过滤器链中】 【注意研读】
	 * SocialAuthenticationProvider 源码
	 * 
	 * @return
	 */
	@Bean
	@Autowired
	public SpringSocialConfigurer socialSecurityConfig(
			SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor,
			SocialProperties socialProperties) {
		// spring social 默认的拦截前缀
		SsoSpringSocialConfigurer configurer = new SsoSpringSocialConfigurer();
		// spring social登陆时需要拦截的url
		configurer.setFilterProcessesUrl(socialProperties.getFilterProcessesUrl());
		// 登陆成功后跳转的页面
		configurer.setSingupUrl(socialProperties.getQq().getRegisterUrl());
		// 1、认证失败跳转注册页面
		// 跳转到signUp controller，从session中获取用户信息并通过生成的uuid保存到redis里面，然后跳转bind页面
		// 前端绑定后发送用户信息到后台bind controller，1）保存到自己系统用户；2）保存一份userconnection表数据，Spring
		// Social通过这里面表数据进行判断是否绑定
		configurer.signupUrl(socialProperties.getQq().getRegisterUrl());
		// 2、认证成功跳转后处理器，跳转带token的成功页面
		configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
		return configurer;
	}

	/**
	 * 构建一个微信连接工厂
	 * 
	 * @param socialProperties
	 * @return
	 */
	@Bean
	@ConditionalOnProperty(prefix = "yishuifengxiao.social.weixin", name = { "appId", "appSecret" })
	public SocialAutoConfigurerAdapter wechatAutoConfigurerAdapter(SocialProperties socialProperties) {
		WechatAutoConfigurerAdapter wechatAutoConfigurerAdapter = new WechatAutoConfigurerAdapter();
		wechatAutoConfigurerAdapter.setSocialProperties(socialProperties);
		return wechatAutoConfigurerAdapter;
	}

}
