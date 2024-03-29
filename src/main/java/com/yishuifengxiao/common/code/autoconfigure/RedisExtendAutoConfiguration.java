package com.yishuifengxiao.common.code.autoconfigure;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.holder.CodeHolder;
import com.yishuifengxiao.common.code.holder.impl.RedisCodeHolder;
import com.yishuifengxiao.common.redis.RedisCoreAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 基于Redis的验证码存储器自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(value = { RedisCoreAutoConfiguration.class, RedisAutoConfiguration.class })
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
	@SuppressWarnings({ "rawtypes" })
	@ConditionalOnBean(value = { RedisTemplate.class }, name = "redisTemplate")
	@ConditionalOnMissingBean({ CodeHolder.class })
	@Bean
	public CodeHolder redisCodeHolder(RedisTemplate redisTemplate, CodeProperties codeProperties) {
		return new RedisCodeHolder(redisTemplate, codeProperties);
	}

}
