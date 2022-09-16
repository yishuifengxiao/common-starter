package com.yishuifengxiao.common.code.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
import com.yishuifengxiao.common.redis.RedisCoreAutoConfiguration;

/**
 * 基于Redis的验证码存储器自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(value = { RedisCoreAutoConfiguration.class })
@ConditionalOnClass({ RedisOperations.class, RedisTemplate.class })
@ConditionalOnProperty(prefix = "yishuifengxiao.code", name = { "enable" }, havingValue = "true", matchIfMissing = false)
public class RedisExtendAutoConfiguration {

	/**
	 * 注入一个名字为codeRepository验证码存储器
	 * 
	 * @param redisTemplate  RedisTemplate
	 * @param codeProperties 验证码属性配置
	 * @return 名字为codeRepository验证码存储器
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ConditionalOnBean(value = { RedisTemplate.class }, name = "redisTemplate")
	@ConditionalOnMissingBean({ CodeRepository.class })
	@Bean
	public CodeRepository redisRepository(RedisTemplate redisTemplate, CodeProperties codeProperties) {
		return new RedisCodeRepository(redisTemplate, codeProperties);
	}

}
