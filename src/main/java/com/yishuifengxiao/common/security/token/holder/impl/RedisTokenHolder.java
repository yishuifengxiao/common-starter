package com.yishuifengxiao.common.security.token.holder.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSONObject;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.datetime.DateTimeUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.ValidateException;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于redis的token存取工具类
 * 
 * @author qingteng
 * @date 2020年11月29日
 * @version 1.0.0
 */
@Slf4j
public class RedisTokenHolder implements TokenHolder {

	/**
	 * redis中存储时的key的前缀
	 */
	private final static String TOKEN_PREFIX = "token_redis_holder_";

	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 获取所有的token<br/>
	 * 按照token的过期时间点从小到到排列
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public synchronized List<SecurityToken> getAll(String key) {
		List<SecurityToken> list = new ArrayList<>();
		Map<Object, Object> map = this.get(key).entries();
		if (null != map) {
			Iterator<Object> it = map.keySet().iterator();
			while (it.hasNext()) {
				Object val = it.next();
				if (null != val) {
					list.add(this.get(key, val.toString()));
				}
			}
		}
		return DataUtil.stream(list).filter(Objects::nonNull).filter(t -> null != t.getExpireAt()).collect(Collectors.toList());
	}

	/**
	 * 保存一个令牌
	 * 
	 * @param token
	 * @throws CustomException
	 */
	@Override
	public synchronized void save(SecurityToken token) throws CustomException {
		this.check(token);
		try {
			this.get(token.getUsername()).put(token.getSessionId(), JSONObject.toJSONString(token));
		} catch (Exception e) {
			log.info("【易水组件】保存令牌{}时出现问题，失败的原因为 {}", token, e.getMessage());
			throw new CustomException(e.getMessage());
		}

	}

	/**
	 * 更新一个令牌
	 * 
	 * @param token
	 * @throws CustomException
	 */
	@Override
	public synchronized void update(SecurityToken token) throws CustomException {
		this.check(token);
		// 先删除再增加
		this.get(token.getUsername()).delete(token.getSessionId());
		this.save(token);
	}

	/**
	 * 根据用户账号和会话id删除一个令牌
	 * 
	 * @param username  用户账号
	 * @param sessionId 会话id
	 * @throws CustomException
	 */
	@Override
	public synchronized void delete(String username, String sessionId) throws CustomException {
		this.get(username).delete(sessionId);
	}

	/**
	 * 根据用户账号和会话id获取一个令牌
	 * 
	 * @param username  用户账号
	 * @param sessionId 会话id
	 * @return 令牌
	 */
	@Override
	public synchronized SecurityToken get(String username, String sessionId) {
		Object data = this.get(username).get(sessionId);
		if (null == data || StringUtils.isBlank(data.toString())) {
			return null;
		}
		try {
			return JSONObject.parseObject(data.toString(), SecurityToken.class);
		} catch (Exception e) {
			log.info("【易水组件】根据用户名{} 和会话{} 获取令牌时失败，失败的原因为 {}", username, sessionId, e.getMessage());
		}
		return null;
	}

	/**
	 * 设置过期时间点
	 * 
	 * @param username 用户账号
	 * @param expireAt 过期时间点
	 * @throws ValidateException 
	 */
	@Override
	public synchronized void setExpireAt(String username, LocalDateTime expireAt) throws ValidateException {
		if(null==expireAt) {
			throw new ValidateException("过期时间点不能为空");
		}
		this.get(username).expireAt(DateTimeUtil.localDateTime2Date(expireAt));
	}

	private BoundHashOperations<String, Object, Object> get(String key) {
		return redisTemplate.boundHashOps(new StringBuffer(TOKEN_PREFIX).append(key).toString());
	}

	/**
	 * 检查令牌的内容合法性
	 * 
	 * @param token
	 * @throws ValidateException
	 */
	private void check(SecurityToken token) throws ValidateException {
		if (null == token) {
			throw new ValidateException("令牌不能为空");
		}
		if (StringUtils.isBlank(token.getUsername())) {
			throw new ValidateException("令牌中必须包含用户账号信息");
		}
		if (StringUtils.isBlank(token.getSessionId())) {
			throw new ValidateException("令牌中必须包含请求识别信息");
		}
		if (null == token.getExpireAt()) {
			throw new ValidateException("令牌中必须包含过期时间信息");
		}
		if (null == token.getValidSeconds()) {
			throw new ValidateException("令牌中必须包含有效时间信息");
		}
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

}
