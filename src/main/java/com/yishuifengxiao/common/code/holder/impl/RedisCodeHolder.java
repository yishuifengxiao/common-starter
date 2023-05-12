package com.yishuifengxiao.common.code.holder.impl;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.holder.CodeHolder;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 验证码redis管理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisCodeHolder implements CodeHolder {

	/**
	 * 简化Redis数据访问代码的Helper类。
	 */
	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;

	/**
	 * 验证码属性配置
	 */
	private CodeProperties codeProperties;

	/**
	 * 存储验证码
	 * 
	 * @param key  验证码的唯一标识符
	 * @param code 需要存储的验证码
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void save(String key, ValidateCode code) {

		// 验证码剩余的有效期
		long expireSecond = LocalDateTime.now().until(code.getExpireTime(), ChronoUnit.SECONDS);
		if (expireSecond > 0) {
			redisTemplate.opsForValue().set(this.getKey(key), code, expireSecond, TimeUnit.SECONDS);
		}

	}

	/**
	 * 根据验证码的唯一标识符获取存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 * @return 存储的验证码
	 */
	@Override
	public ValidateCode get(String key) {

		return (ValidateCode) redisTemplate.opsForValue().get(this.getKey(key));
	}

	/**
	 * 根据验证码的唯一标识符移除存储的验证码
	 * 
	 * @param key 验证码的唯一标识符
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void remove(String key) {
		redisTemplate.delete(this.getKey(key));

	}

	/**
	 * 生成key值
	 * 
	 * @param key
	 * @return
	 */

	/**
	 * 获取操作Redis时的键
	 * 
	 * @param key 验证码的唯一标识符
	 * @return 操作Redis时的键
	 */
	private String getKey(String key) {
		return new StringBuilder(this.codeProperties.getPrefix()).append(":").append(key).toString();
	}

	@SuppressWarnings("rawtypes")
	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	@SuppressWarnings({ "rawtypes" })
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisCodeHolder() {

	}

	@SuppressWarnings({ "rawtypes" })
	public RedisCodeHolder(RedisTemplate redisTemplate, CodeProperties codeProperties) {
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
