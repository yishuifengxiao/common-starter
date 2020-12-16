package com.yishuifengxiao.common.oauth2.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 配置spring security密钥存储
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class, RedisOperations.class, EnableAuthorizationServer.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.security", name = { "enable" }, havingValue = "true",matchIfMissing=true)
public class SecurityRedisAutoConfiguration {

	@ConditionalOnMissingBean(name = { "tokenStore" })
	@Bean("tokenStore")
	public TokenStore tokenStore(RedisConnectionFactory connectionFactory) {
		return new RedisTokenStore(connectionFactory);
	}

}
