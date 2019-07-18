package com.yishuifengxiao.common.autoconfigure;

import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.RedisCodeRepository;

@Configuration
@ConditionalOnClass(RedisOperations.class)
public class RedisExtendAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	/**
	 * 验证码redis管理器
	 * 
	 * @return
	 */
	@ConditionalOnBean(name = "redisTemplate")
	@Bean("codeRepository")
	public CodeRepository redisRepository(RedisTemplate<String, Object> redisTemplate) {
		return new RedisCodeRepository(redisTemplate);
	}
}
