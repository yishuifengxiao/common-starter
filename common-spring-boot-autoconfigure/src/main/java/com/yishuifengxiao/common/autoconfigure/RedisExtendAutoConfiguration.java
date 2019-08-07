package com.yishuifengxiao.common.autoconfigure;

import java.net.UnknownHostException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.yishuifengxiao.common.validation.repository.CodeRepository;
import com.yishuifengxiao.common.validation.repository.impl.RedisCodeRepository;

@Configuration
@ConditionalOnClass(RedisOperations.class)
public class RedisExtendAutoConfiguration {

	/**
	 * 定义一个redisValueSerializer
	 * 
	 * @return
	 */
	@Bean("redisValueSerializer")
	@ConditionalOnMissingBean(name = "redisValueSerializer")
	public RedisSerializer<Object> redisValueSerializer() {

		// Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new
		// Jackson2JsonRedisSerializer<>(
		// Object.class);

		// 解决查询缓存转换异常的问题
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		// 支持java8的日期时间
		mapper.registerModule(new JavaTimeModule()).registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module());
		mapper.findAndRegisterModules();
		// 反序列化时去掉多余的字段
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// jackson2JsonRedisSerializer.setObjectMapper(mapper);

		return new GenericJackson2JsonRedisSerializer(mapper);
	}

	@Autowired
	private RedisSerializer<Object> redisValueSerializer;

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		RedisSerializer<String> stringRedisSerializer = new StringRedisSerializer();

		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(redisValueSerializer);
		template.setHashKeySerializer(stringRedisSerializer);
		template.setHashValueSerializer(redisValueSerializer);
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
	public RedisCacheConfiguration redisCacheConfiguration() {
		//@formatter:off  
		// 配置序列化（解决乱码的问题）
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
		configuration = configuration
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(redisValueSerializer))// spring Security 默认不支持 jackson的序列化
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
				.entryTtl(Duration.ofMinutes(30L));
		//@formatter:on
		return configuration;
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
