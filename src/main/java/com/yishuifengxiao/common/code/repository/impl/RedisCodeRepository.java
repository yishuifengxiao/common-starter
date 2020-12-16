package com.yishuifengxiao.common.code.repository.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.repository.CodeRepository;

/**
 * 验证码redis管理器
 * 
 * @author yishui
 * @date 2019年6月23日
 * @version 0.0.1
 */
public class RedisCodeRepository implements CodeRepository {

	private RedisTemplate<String, Object> redisTemplate;

	private CodeProperties codeProperties;

	@Override
	public void save(ServletWebRequest request, String key, ValidateCode code) {

		// 验证码剩余的有效期
		long expireSecond = LocalDateTime.now().until(code.getExpireTime(), ChronoUnit.SECONDS);
		if (expireSecond > 0) {
			redisTemplate.opsForValue().set(this.codeProperties.getPrefix() + key, code, expireSecond,
					TimeUnit.SECONDS);
		}

	}

	@Override
	public ValidateCode get(ServletWebRequest request, String key) {

		return (ValidateCode) redisTemplate.opsForValue().get(this.codeProperties.getPrefix() + key);
	}

	@Override
	public void remove(ServletWebRequest request, String key) {
		redisTemplate.delete(this.codeProperties.getPrefix() + key);

	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisCodeRepository() {

	}

	public RedisCodeRepository(RedisTemplate<String, Object> redisTemplate, CodeProperties codeProperties) {
		this.redisTemplate = redisTemplate;
		this.codeProperties = codeProperties;
	}

	public CodeProperties getCodeProperties() {
		return codeProperties;
	}

	public void setCodeProperties(CodeProperties codeProperties) {
		this.codeProperties = codeProperties;
	}

}
