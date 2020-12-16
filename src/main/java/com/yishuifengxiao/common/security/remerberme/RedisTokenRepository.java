/**
 * 
 */
package com.yishuifengxiao.common.security.remerberme;

import java.util.Date;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 实现记住我功能
 * 
 * @author yishui
 * @date 2018年11月23日
 * @Version 0.0.1
 */
public class RedisTokenRepository implements PersistentTokenRepository {

	/**
	 * 默认的前缀
	 */
	private final static String KEY = "remember_me_token";

	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void createNewToken(PersistentRememberMeToken token) {

		PersistentRememberMeToken current = (PersistentRememberMeToken) redisTemplate.opsForHash().get(KEY,
				token.getSeries());

		if (current != null) {
			throw new DataIntegrityViolationException("Series Id '" + token.getSeries() + "' already exists!");
		}
		redisTemplate.opsForHash().put(KEY, token.getSeries(), token);
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		PersistentRememberMeToken token = getTokenForSeries(series);

		PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(), series, tokenValue,
				new Date());
		redisTemplate.opsForHash().put(KEY, token.getSeries(), newToken);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		return (PersistentRememberMeToken) redisTemplate.opsForHash().get(KEY, seriesId);
	}

	@Override
	public void removeUserTokens(String username) {
		Set<Object> hashKeys = redisTemplate.opsForHash().keys(KEY);
		if (hashKeys != null) {
			hashKeys.parallelStream().filter(t -> t != null).map(t -> t.toString()).forEach(t -> {
				PersistentRememberMeToken token =getTokenForSeries(t);
				if(token!=null&&username.equals(token.getUsername())) {
					redisTemplate.opsForHash().delete(KEY, t);
				}
			});
		}
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisTokenRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisTokenRepository() {
	}

}
