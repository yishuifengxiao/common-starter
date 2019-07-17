package com.yishuifengxiao.common.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.yishuifengxiao.common.security.remerberme.RedisTokenRepository;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.RedisCodeRepository;

@Configuration
@ConditionalOnBean(name = "redisTemplate")
public class RedisExtendAutoConfiguration {
	/**
	 * 记住密码策略【存储在redis数据库中】
	 * 
	 * @return
	 */
	@Bean("persistentTokenRepository")
	@ConditionalOnBean(name = "redisTemplate")
	public PersistentTokenRepository redisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
		RedisTokenRepository redisTokenRepository = new RedisTokenRepository();
		redisTokenRepository.setRedisTemplate(redisTemplate);
		return new RedisTokenRepository();
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
