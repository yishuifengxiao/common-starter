package com.yishuifengxiao.common.validation.repository.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.validation.entity.ValidateCode;
import com.yishuifengxiao.common.validation.repository.CodeRepository;

/**
 * 验证码redis管理器
 * 
 * @author yishui
 * @date 2019年6月23日
 * @version 0.0.1
 */
public class RedisCodeRepository implements CodeRepository {
	/**
	 * 默认的前缀
	 */
	private final static String PREFIX = "validate_code_";

	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void save(ServletWebRequest request, String key, ValidateCode code) {

		// 验证码剩余的有效期
		long expireSecond = LocalDateTime.now().until(code.getExpireTime(), ChronoUnit.SECONDS);
		if (expireSecond > 0) {
			redisTemplate.opsForValue().set(PREFIX + key, code, expireSecond, TimeUnit.SECONDS);
		}

	}

	@Override
	public ValidateCode get(ServletWebRequest request, String key) {

		return (ValidateCode) redisTemplate.opsForValue().get(PREFIX + key);
	}

	@Override
	public void remove(ServletWebRequest request, String key) {
		redisTemplate.delete(PREFIX + key);

	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisCodeRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisCodeRepository() {

	}

}
