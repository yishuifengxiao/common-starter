package com.yishuifengxiao.common.validation.repository.impl;

import java.time.LocalDateTime;
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

	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void save(ServletWebRequest request, String key, ValidateCode code) {
		// 验证码剩余的有效期
		int expireSecond = code.getExpireTime().getSecond() - LocalDateTime.now().getSecond();
		if (expireSecond > 0) {
			redisTemplate.opsForValue().set(key, code, expireSecond + 0L, TimeUnit.SECONDS);
		}

	}

	@Override
	public ValidateCode get(ServletWebRequest request, String key) {

		return (ValidateCode) redisTemplate.opsForValue().get(key);
	}

	@Override
	public void remove(ServletWebRequest request, String key) {
		redisTemplate.delete(key);

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
