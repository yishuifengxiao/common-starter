package com.yishuifengxiao.common.autoconfigure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.yishuifengxiao.common.security.remerberme.RedisTokenRepository;

@Configuration
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, EnableWebSecurity.class,
	WebSecurityConfigurerAdapter.class ,RedisOperations.class})
public class PersistentTokenAutoConfiguration {
	/**
	 * 记住密码策略【存储在redis数据库中】
	 * 
	 * @return
	 */
	@Bean("persistentTokenRepository")
	@ConditionalOnBean(name = "redisTemplate")
	@ConditionalOnClass(DefaultAuthenticationEventPublisher.class)
	public PersistentTokenRepository redisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
		RedisTokenRepository redisTokenRepository = new RedisTokenRepository();
		redisTokenRepository.setRedisTemplate(redisTemplate);
		return new RedisTokenRepository();
	}
}
