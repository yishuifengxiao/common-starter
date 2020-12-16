package com.yishuifengxiao.common.redis;

import java.net.UnknownHostException;
import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yishuifengxiao.common.code.CodeAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * 注入redis相关的配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisOperations.class)
@AutoConfigureBefore(value = { CodeAutoConfiguration.class })
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnProperty(prefix = "yishuifengxiao.redis", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class RedisCoreAutoConfiguration {

	/**
	 * 定义一个redisValueSerializer
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@Bean("redisValueSerializer")
	@ConditionalOnMissingBean(name = "redisValueSerializer")
	public RedisSerializer<Object> redisValueSerializer() {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 反序列化时候遇到不匹配的属性并不抛出异常
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// 序列化时候遇到空对象不抛出异常
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		// 反序列化的时候如果是无效子类型,不抛出异常
		objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		// 不使用默认的dateTime进行序列化,
		objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
		// 使用JSR310提供的序列化类,里面包含了大量的JDK8时间序列化类
		objectMapper.registerModule(new JavaTimeModule());
		// 启用反序列化所需的类型信息,在属性中添加@class
//		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
//				com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,  JsonTypeInfo.As.PROPERTY);
		// 配置null值的序列化器
//		GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);

		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			RedisSerializer<Object> redisValueSerializer) throws UnknownHostException {
		RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();

		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(redisValueSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(redisValueSerializer);
		template.setDefaultSerializer(stringRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	/**
	 * 自定义一个名字为springSessionDefaultRedisSerializer 的序列化器<br/>
	 * 参见
	 * org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
	 * 的188行
	 * 
	 * @return
	 */
	@Bean("springSessionDefaultRedisSerializer")
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new JdkSerializationRedisSerializer();

	}

	/**
	 * 配置序列化（解决乱码的问题）
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public RedisCacheConfiguration redisCacheConfiguration(RedisSerializer<Object> redisValueSerializer) {
		//@formatter:off  
		// 配置序列化（解决乱码的问题）
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
		configuration = configuration
				.serializeValuesWith(RedisSerializationContext.SerializationPair
				// spring Security 默认不支持 jackson的序列化
				.fromSerializer(redisValueSerializer))
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.entryTtl(Duration.ofMinutes(30L));
		//@formatter:on
		return configuration;
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <Redis相关配置> 相关的配置");
	}

}
