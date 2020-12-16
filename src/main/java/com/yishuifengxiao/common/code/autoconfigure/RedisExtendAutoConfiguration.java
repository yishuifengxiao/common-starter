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
 * 注入redis相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Configuration
@ConditionalOnClass(RedisOperations.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.code", name = { "enable" }, havingValue = "true", matchIfMissing = true)
public class RedisExtendAutoConfiguration {

	/**
	 * 验证码redis管理器
	 * 
	 * @return
	 */
	@ConditionalOnBean(name = "redisTemplate")
	@ConditionalOnMissingBean(name = { "codeRepository" })
	@Bean("codeRepository")
	public CodeRepository redisRepository(RedisTemplate<String, Object> redisTemplate,CodeProperties codeProperties) {
		return new RedisCodeRepository(redisTemplate,codeProperties);
	}

}
