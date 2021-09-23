package com.yishuifengxiao.common.security.support;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.event.TokenExpireEvnet;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.context.SessionStorage;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.TokenException;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.tool.utils.NumberUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统安全信息处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleSecurityHelper implements SecurityHelper {

	private PropertyResource propertyResource;

	private UserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder;

	/**
	 * token生成器
	 */
	private TokenBuilder tokenBuilder;

	/**
	 * 是否显示加载日志
	 */
	private boolean show = false;

	@Override
	public SecurityToken createUnsafe(String username, String sessionId) throws CustomException {

		return this.createUnsafe(username, sessionId, propertyResource.security().getToken().getValidSeconds());
	}

	@Override
	public SecurityToken createUnsafe(String username, String sessionId, int validSeconds) throws CustomException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException("账号不能为空");
		}

		UserDetails userDetails = this.loadUserByUsername(username.trim());
		return this.create(userDetails, sessionId, validSeconds,
				propertyResource.security().getToken().getPreventsLogin(),
				propertyResource.security().getToken().getMaxSessions());

	}

	@Override
	public SecurityToken create(String username, String password, String sessionId) throws CustomException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException("账号不能为空");
		}

		if (StringUtils.isBlank(password)) {
			password = "";
		}

		UserDetails userDetails = this.authorize(username.trim(), password);

		return this.create(userDetails, sessionId);
	}

	/**
	 * <p>
	 * 根据指定参数生成访问令牌
	 * </p>
	 * 
	 * @param userDetails 用户认证信息
	 * @param sessionId   会话id
	 * @return 访问令牌
	 * @throws CustomException 创建时发生问题
	 */
	private SecurityToken create(UserDetails userDetails, String sessionId) throws CustomException {

		return this.create(userDetails, sessionId, propertyResource.security().getToken().getValidSeconds(),
				propertyResource.security().getToken().getPreventsLogin(),
				propertyResource.security().getToken().getMaxSessions());

	}

	/**
	 * <p>
	 * 根据指定参数生成访问令牌
	 * </p>
	 * 
	 * @param userDetails   用户认证信息
	 * @param sessionId     会话id
	 * @param validSeconds  令牌过期时间，单位为秒
	 * @param preventsLogin 在达到同一个账号最大的登陆数量时是否阻止后面的用户登陆,默认为false
	 * @param maxSessions   同一个账号最大的登陆数量
	 * @return 访问令牌
	 * @throws CustomException 创建时发生问题
	 */
	private SecurityToken create(UserDetails userDetails, String sessionId, int validSeconds, boolean preventsLogin,
			int maxSessions) throws CustomException {

		if (null == userDetails) {
			throw new TokenException(ErrorCode.NO_USERDETAILS,
					propertyResource.security().getMsg().getUserDetailsIsNull());
		}

		if (StringUtils.isBlank(sessionId)) {
			sessionId = userDetails.getUsername();
		}

		if (!NumberUtil.greaterZero(validSeconds)) {
			validSeconds = TokenConstant.TOKEN_VALID_TIME_IN_SECOND;
		}

		if (!NumberUtil.greaterZero(maxSessions)) {
			maxSessions = TokenConstant.MAX_SESSION_NUM;
		}

		// 检查用户信息
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		// 根据用户信息生成一个访问令牌
		SecurityToken token = tokenBuilder.creatNewToken(authentication.getName(), sessionId, validSeconds,
				preventsLogin, maxSessions);

		// 将认证信息注入到spring Security中
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return token;

	}

	@Override
	public Authentication authorize(String tokenValue) throws CustomException {
		if (StringUtils.isBlank(tokenValue)) {
			throw new ValidateException(propertyResource.security().getMsg().getTokenValueIsNull());
		}

		// 解析token
		SecurityToken token = tokenBuilder.loadByTokenValue(tokenValue);

		if (show) {
			log.info("【易水组件】根据访问令牌 {} 获取到的认证信息为 {}", tokenValue, token);
		}

		CustomException e = null;

		if (null == token) {
			e = new CustomException(ErrorCode.INVALID_TOKEN, propertyResource.security().getMsg().getTokenIsNull());

			SpringContext.publishEvent(new TokenExpireEvnet(this, e, token, tokenValue));

			throw e;
		}

		if (token.isExpired()) {
			if (show) {
				log.info("【易水组件】访问令牌 {} 已过期 ", token);
			}
			// 删除失效的token
			tokenBuilder.remove(tokenValue);

			e = new CustomException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsExpired());

			SpringContext.publishEvent(new TokenExpireEvnet(this, e, token, tokenValue));
			throw e;
		}

		if (!token.isActive()) {
			if (show) {
				log.info("【易水组件】访问令牌 {} 已失效 ", token);
			}
			// 删除失效的token
			tokenBuilder.remove(tokenValue);

			e = new CustomException(ErrorCode.EXPIRED_ROKEN, propertyResource.security().getMsg().getTokenIsInvalid());

			SpringContext.publishEvent(new TokenExpireEvnet(this, e, token, tokenValue));
			throw e;
		}

		// 获取认证状态
		UserDetails userDetails = this.loadUserByUsername(token.getUsername());

		// 刷新令牌的过期时间
		token = tokenBuilder.refreshExpireTime(tokenValue);

		// 存储访问令牌
		SessionStorage.put(token);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		return authentication;
	}

	@Override
	public UserDetails authorize(String username, String password) throws CustomException {
		UserDetails userDetails = this.loadUserByUsername(username);

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new TokenException(ErrorCode.PASSWORD_ERROR,
					propertyResource.security().getMsg().getPasswordIsError());
		}

		return userDetails;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws CustomException {
		if (StringUtils.isBlank(username)) {
			throw new ValidateException("账号不能为空");
		}
		// 获取认证信息
		UserDetails userDetails = userDetailsService.loadUserByUsername(username.trim());

		if (null == userDetails) {
			throw new TokenException(ErrorCode.USERNAME_NO_EXTIS,
					propertyResource.security().getMsg().getAccountNoExtis());
		}

		if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
			throw new TokenException(ErrorCode.ACCOUNT_EXPIRED,
					propertyResource.security().getMsg().getAccountExpired());
		}

		if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
			throw new TokenException(ErrorCode.ACCOUNT_LOCKED, propertyResource.security().getMsg().getAccountLocked());
		}

		if (BooleanUtils.isFalse(userDetails.isCredentialsNonExpired())) {
			throw new TokenException(ErrorCode.PASSWORD_EXPIRED,
					propertyResource.security().getMsg().getPasswordExpired());
		}

		if (BooleanUtils.isFalse(userDetails.isEnabled())) {
			throw new TokenException(ErrorCode.ACCOUNT_UNENABLE,
					propertyResource.security().getMsg().getAccountNoEnable());
		}
		return userDetails;
	}

	public SimpleSecurityHelper(PropertyResource propertyResource, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder, TokenBuilder tokenBuilder) {
		this.propertyResource = propertyResource;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.tokenBuilder = tokenBuilder;
		this.show = BooleanUtils.isTrue(propertyResource.security().getShowDeatil());
	}

}