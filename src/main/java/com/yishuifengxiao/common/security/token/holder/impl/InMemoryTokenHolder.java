package com.yishuifengxiao.common.security.token.holder.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 基于内存的token存取工具类
 * 
 * @author qingteng
 * @date 2020年11月29日
 * @version 1.0.0
 */
public class InMemoryTokenHolder implements TokenHolder {

	private final Map<String, List<SecurityToken>> map = new HashMap<>();

	/**
	 * 获取所有的token
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public synchronized List<SecurityToken> getAll(String key) {
		return DataUtil.stream(map.get(key)).filter(Objects::nonNull).filter(t -> null != t.getExpireAt())
				.collect(Collectors.toList());
	}

	/**
	 * 保存一个token<br/>
	 * 
	 * @param token
	 */
	@Override
	public synchronized void save(SecurityToken token) throws CustomException {
		this.check(token);
		List<SecurityToken> tokens = this.getAll(token.getUsername());
		tokens.add(token);
		map.remove(token.getUsername());
		map.put(token.getUsername(), tokens);

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
		// 先删除
		this.delete(token.getUsername(), token.getSessionId());
		// 再新增
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
		List<SecurityToken> tokens = DataUtil.stream(this.getAll(username)).filter(Objects::nonNull)
				.filter(t -> !StringUtils.equalsIgnoreCase(t.getSessionId(), sessionId)).collect(Collectors.toList());
		map.remove(username);
		map.put(username, tokens);
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
		List<SecurityToken> tokens = DataUtil.stream(this.getAll(username)).filter(Objects::nonNull)
				.filter(t -> StringUtils.equalsIgnoreCase(t.getSessionId(), sessionId)).collect(Collectors.toList());
		return DataUtil.first(tokens);
	}

	/**
	 * 设置过期时间点
	 * 
	 * @param username 用户账号
	 * @param expireAt 过期时间点
	 */
	@Override
	public synchronized void setExpireAt(String username, LocalDateTime expireAt) {
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

}
