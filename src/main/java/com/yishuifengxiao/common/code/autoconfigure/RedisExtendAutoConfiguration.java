package com.yishuifengxiao.common.code.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.repository.CodeRepository;
import com.yishuifengxiao.common.code.repository.impl.RedisCodeRepository;

/**
 * 基于Redis的验证码存储器自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.code", name = { "enable" }, havingValue = "true", matchIfMissing = true)
public class RedisExtendAutoConfiguration {

	/**
	 * 注入一个名字为codeRepository验证码存储器
	 * 
	 * @param redisTemplate  RedisTemplate
	 * @param codeProperties 验证码属性配置
	 * @return 名字为codeRepository验证码存储器
	 */
	@ConditionalOnBean(name = "redisTemplate")
	@ConditionalOnMissingBean({ CodeRepository.class })
	@Bean
	public CodeRepository redisRepository(RedisTemplate<String, Object> redisTemplate, CodeProperties codeProperties) {
		return new RedisCodeRepository(redisTemplate, codeProperties);
	}

}
