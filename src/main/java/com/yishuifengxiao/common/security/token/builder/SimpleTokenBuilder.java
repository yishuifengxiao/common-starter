/**
 * 
 */
package com.yishuifengxiao.common.security.token.builder;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.encoder.DES;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于Redis实现的token生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleTokenBuilder implements TokenBuilder {

	/**
	 * token存储工具<br/>
	 * 键：username<br/>
	 * 值：该用户所有的token
	 */
	private TokenHolder tokenHolder;

	
	/**
	 * 创建一个新的token
	 * 
	 * @param username      用户名
	 * @param sessionId     会话id
	 * @param validSeconds  token的有效时间，单位为秒
	 * @param preventsLogin 在达到最大的token数量限制时是否阻止后面的用户登陆
	 * @param maxSessions   最大的token数量
	 * @return SecurityToken 生成的token
	 * @throws CustomException 生成时出现了问题
	 */
	@Override
	public synchronized SecurityToken creatNewToken(String username, String sessionId, Integer validSeconds,
			boolean preventsLogin, int maxSessions) throws CustomException {
		if (StringUtils.isBlank(username)) {
			throw new CustomException (ErrorCode.USERNAME_NULL, "用户名不能为空");
		}

		if (StringUtils.isBlank(sessionId)) {
			throw new CustomException (ErrorCode.SESSION_ID_NULL, "会话id不能为空");
		}
		if (maxSessions <= 0) {
			maxSessions = TokenConstant.MAX_SESSION_NUM;
		}

		// 先判断改token是否存在
		SecurityToken token = tokenHolder.get(username, sessionId);
		if (null != token) {
			// token已经存在
			if (token.isAvailable()) {
				// 当前token还有效

				// 重置有效期
				token.refreshExpireTime();
				// 更新token
				tokenHolder.update(token);
				// 更新有效期
				tokenHolder.setExpireAt(token.getUsername(), token.getExpireAt());
				return token;

			} else {
				// token已经失效,删除旧的token信息
				tokenHolder.delete(username, sessionId);
			}
		}

		return createNewToken(username, sessionId, validSeconds, preventsLogin, maxSessions);
	}

	/**
	 * 新生成一个token
	 * 
	 * @param username      用户账号
	 * @param sessionId     会话id
	 * @param validSeconds  token有效期
	 * @param preventsLogin 是否阻止后面的用户登陆
	 * @param maxSessions   最大登陆数量限制
	 * @return
	 * @throws CustomException 
	 * @throws CustomException
	 */
	private SecurityToken createNewToken(String username, String sessionId, Integer validSeconds, boolean preventsLogin,
			int maxSessions) throws CustomException , CustomException {
		// 先取出该用户所有可用的token
		List<SecurityToken> list = this.loadAllToken(username, true);

		if (null == list) {
			list = new ArrayList<>();
		}

		String tokenValue = DES
				.encrypt(new StringBuffer(username).append(TokenConstant.TOKEN_SPLIT_CHAR).append(sessionId)
						.append(TokenConstant.TOKEN_SPLIT_CHAR).append(System.currentTimeMillis()).toString());

		SecurityToken newToken = new SecurityToken(tokenValue, username, sessionId, validSeconds);

		if (list.size() >= maxSessions) {
			if (preventsLogin) {
				throw new CustomException (ErrorCode.MAX_USER_LIMT, "已达到最大登陆用户数量限制");
			}
			// 将第一个token设置为失效状态
			SecurityToken extisToken = list.get(0);
			extisToken.setActive(false);
			// 覆盖掉旧的列表
			tokenHolder.update(extisToken);
		}
		// 新增一个token
		tokenHolder.save(newToken);
		// 更新有效期
		tokenHolder.setExpireAt(username, newToken.getExpireAt());
		return newToken;
	}

	@Override
	public synchronized List<SecurityToken> loadAllToken(String username, boolean isOnlyAvailable) {
		// 获取该用户所有的token信息
		List<SecurityToken> list = DataUtil.stream(tokenHolder.getAll(username)).filter(Objects::nonNull).distinct()
				.collect(Collectors.toList());
		if (isOnlyAvailable) {
			list = DataUtil.stream(list).filter(SecurityToken::isAvailable).collect(Collectors.toList());
		}
		return DataUtil.stream(list).sorted((t1, t2) -> t1.getExpireAt().isAfter(t2.getExpireAt()) ? 1 : -1)
				.collect(Collectors.toList());
	}

	@Override
	public synchronized int getTokenNum(String username, boolean isActive, boolean isExpired) {
		List<SecurityToken> list = this.loadAllToken(username, false);
		return (int) DataUtil.stream(list).filter(t -> t.isActive() == isActive && t.isExpired() == isExpired).count();
	}

	@Override
	public synchronized String[] parseTokenValue(String tokenValue) throws CustomException {
		try {
			tokenValue = DES.decrypt(tokenValue);
		} catch (Exception e) {
			log.info("【易水组件】解密tokenValue时出现问题，出现问题的原因为{}", e.getMessage());
			throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
		}

		if (StringUtils.isBlank(tokenValue)) {
			throw new CustomException(ErrorCode.TOKEN_VALUE_NULL, "非法的认证信息");
		}
		// 非法的tokenValue
		if (!StringUtils.contains(tokenValue, TokenConstant.TOKEN_SPLIT_CHAR)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
		}

		String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(tokenValue,
				TokenConstant.TOKEN_SPLIT_CHAR);
		// 非法的tokenValue
		if (null == tokens || tokens.length != TokenConstant.TOKEN_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
		}

		return tokens;
	}

	@Override
	public synchronized SecurityToken loadByTokenValue(String tokenValue) throws CustomException {
		try {
			String[] tokens = this.parseTokenValue(tokenValue);
			return tokenHolder.get(tokens[0], tokens[1]);

		} catch (Exception e) {
			log.info("【易水组件】根据tokenValue获取token时出现问题，出现问题的原因为{}", e.getMessage());
		}

		return null;
	}

	@Override
	public synchronized void remove(String tokenValue) throws CustomException {
		String[] tokens = this.parseTokenValue(tokenValue);
		tokenHolder.delete(tokens[0], tokens[1]);
	}

	@Override
	public synchronized SecurityToken refreshExpireTime(String tokenValue) throws CustomException {
		SecurityToken token = this.loadByTokenValue(tokenValue);
		if (null != token && token.isAvailable()) {
			token.refreshExpireTime();
			// 更新token信息
			tokenHolder.update(token);
			// 更新过期时间
			tokenHolder.setExpireAt(token.getUsername(), token.getExpireAt());
		}
		return token;
	}

	public TokenHolder getTokenHolder() {
		return tokenHolder;
	}

	public void setTokenHolder(TokenHolder tokenHolder) {
		this.tokenHolder = tokenHolder;
	}

}
