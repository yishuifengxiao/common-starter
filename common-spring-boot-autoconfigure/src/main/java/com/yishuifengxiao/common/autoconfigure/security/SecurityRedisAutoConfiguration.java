package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
		WebSecurityConfigurerAdapter.class, RedisOperations.class, EnableAuthorizationServer.class })
public class SecurityRedisAutoConfiguration {

	@ConditionalOnMissingBean(name = { "tokenStore" })
	@Bean("tokenStore")
	public TokenStore tokenStore(RedisConnectionFactory connectionFactory) {
		return new RedisTokenStore(connectionFactory);
	}

}
