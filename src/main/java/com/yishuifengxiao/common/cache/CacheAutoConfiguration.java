package com.yishuifengxiao.common.cache;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 缓存扩展支持自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(CacheManager.class)
public class CacheAutoConfiguration {

	/**
	 * 注入名为simpleKeyGenerator的缓存key生成器
	 * 
	 * @return 缓存key生成器
	 */
	@Bean("simpleKeyGenerator")
	@ConditionalOnMissingBean(name = { "simpleKeyGenerator" })
	public KeyGenerator simpleKeyGenerator() {
		return new SimpleKeyGenerator();
	}

	/**
	 * 配置检查
	 */
	@PostConstruct
	public void checkConfig() {

		log.trace("【yishuifengxiao-common-spring-boot-starter】: 开启 <缓存扩展支持> 相关的配置");
	}

}
