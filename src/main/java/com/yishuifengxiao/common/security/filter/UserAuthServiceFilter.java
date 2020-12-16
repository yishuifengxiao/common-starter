package com.yishuifengxiao.common.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.security.utils.SecurityHolder;
import com.yishuifengxiao.common.tool.context.SessionStorage;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户认证逻辑<br/>
 * 用于在非oauth2的情况下，在仅仅使用spring Security时系统从用户提供的请求里解析出认证信息，判断用户是否能够认证<br/>
 * 在此情况下，除了忽视资源和非管理资源不需要经过该逻辑，理论上一版情况下其他资源都要经过该逻辑
 * 
 * @author qingteng
 * @date 2020年11月26日
 * @version 1.0.0
 */
@Slf4j
public class UserAuthServiceFilter extends OncePerRequestFilter implements InitializingBean {

	private Map<String, AntPathRequestMatcher> map = new HashMap<>();

	private PropertyResource propertyResource;

	private HandlerProcessor handlerProcessor;

	private TokenBuilder tokenBuilder;

	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 先判断请求是否需要经过授权校验

		boolean requiresAuthentication = this.requiresAuth(request);
		log.debug("【易水组件】请求 {} 是否需要进行校验校验的结果为 {}", request.getRequestURI(), requiresAuthentication);
		if (requiresAuthentication) {

			try {
				Authentication authentication = this.authorize(request);
				if (null == authentication) {
					// 将异常信息存储到上下文中
					CustomException exception = new CustomException(ErrorCode.AUTH_NULL, "请求需要认证");
					SecurityHolder.setException(exception);
					handlerProcessor.preAuth(request, response,
							Response.of(Response.Const.CODE_UNAUTHORIZED, "请求需要认证", exception));
					return;
				}
				// 将认证信息注入到spring Security中
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (CustomException e) {
				handlerProcessor.preAuth(request, response,
						Response.of(Response.Const.CODE_UNAUTHORIZED, e.getMessage(), e));
				return;
			} catch (Exception e) {
				handlerProcessor.exception(request, response, e);
				return;
			}

		}
		filterChain.doFilter(request, response);
	}

	/**
	 * 从请求里取出认证信息
	 * 
	 * @param request
	 * @return
	 * @throws CustomException
	 */
	private Authentication authorize(HttpServletRequest request) throws CustomException {
		String tokenValue = this.getTokenValueInHeader(request);
		if (StringUtils.isBlank(tokenValue)) {
			tokenValue = this.getTokenValueInQuery(request);
		}
		if (StringUtils.isBlank(tokenValue)) {
			throw new CustomException(ErrorCode.TOKEN_VALUE_NULL, "请携带认证信息");
		}
		// 解析token
		SecurityToken token = tokenBuilder.loadByTokenValue(tokenValue);

		log.debug("【易水组件】根据访问令牌 {} 获取到的认证信息为 {}", tokenValue, token);

		if (null == token) {
			throw new CustomException(ErrorCode.INVALID_TOKEN, "认证信息无效或登陆状态已过期");
		}

		if (token.isExpired()) {
			// 删除失效的token
			tokenBuilder.remove(tokenValue);
			throw new CustomException(ErrorCode.EXPIRED_ROKEN, "登陆状态已过期");
		}

		if (!token.isActive()) {
			// 删除失效的token
			tokenBuilder.remove(tokenValue);

			throw new CustomException(ErrorCode.EXPIRED_ROKEN, "登陆状态已失效");
		}

		// 获取认证状态
		UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUsername());
		// 账号状态检查
		check(userDetails);

		// 刷新令牌的过期时间
		token = tokenBuilder.refreshExpireTime(tokenValue);

		// 存储访问令牌
		SessionStorage.put(token);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		return authentication;
	}

	/***
	 * 账号状态检查
	 * 
	 * @param userDetails
	 * @throws CustomException
	 */
	private void check(UserDetails userDetails) throws CustomException {
		if (null == userDetails) {
			throw new CustomException(ErrorCode.USERNAME_NO_EXTIS, "用户名不存在");
		}

		if (BooleanUtils.isFalse(userDetails.isAccountNonExpired())) {
			throw new CustomException(ErrorCode.ACCOUNT_EXPIRED, "账号已过期");
		}
		if (BooleanUtils.isFalse(userDetails.isAccountNonLocked())) {
			throw new CustomException(ErrorCode.ACCOUNT_LOCKED, "账号已锁定");
		}
		if (BooleanUtils.isFalse(userDetails.isCredentialsNonExpired())) {
			throw new CustomException(ErrorCode.PASSWORD_EXPIRED, "密码已过期");
		}
		if (BooleanUtils.isFalse(userDetails.isEnabled())) {
			throw new CustomException(ErrorCode.ACCOUNT_UNENABLE, "账号未启用");
		}
	}

	/**
	 * 从请求参数里获取tokenValue
	 * 
	 * @param request
	 * @return
	 */
	private String getTokenValueInQuery(HttpServletRequest request) {
		String requestParamter = propertyResource.security().getToken().getRequestParamter();
		if (StringUtils.isBlank(requestParamter)) {
			requestParamter = TokenConstant.TOKEN_REQUEST_PARAM;
		}

		String tokenValue = request.getParameter(requestParamter);

		if (StringUtils.isBlank(tokenValue)) {
			tokenValue = (String) request.getSession().getAttribute(requestParamter);
		}
		return tokenValue;
	}

	/**
	 * 从请求头里获取到tokenValue
	 * 
	 * @param request
	 * @return tokenValue
	 */
	private String getTokenValueInHeader(HttpServletRequest request) {
		String headerParamter = propertyResource.security().getToken().getHeaderParamter();
		if (StringUtils.isBlank(headerParamter)) {
			headerParamter = TokenConstant.TOKEN_HEADER_PARAM;
		}
		return request.getHeader(headerParamter);
	}

	/**
	 * 判断请求是否需要授权认证
	 * 
	 * @param request
	 * @return true表示需要授权认证，false表示不需要授权认证
	 * @throws ExecutionException
	 */
	private boolean requiresAuth(HttpServletRequest request) {

		try {
			for (String url : propertyResource.getAllUnCheckUrls()) {
				if (this.getMatcher(url).matches(request)) {
					return false;
				}
			}
		} catch (Exception e) {
			log.info("【易水组件】判断请求是否需要授权认证时出现问题，出现问题的原因为 {}", e.getMessage());
		}

		return true;

	}

	/**
	 * 根据url获取匹配器
	 * 
	 * @param url
	 * @return
	 */
	private synchronized AntPathRequestMatcher getMatcher(String url) {
		AntPathRequestMatcher matcher = map.get(url);
		if (null == matcher) {
			matcher = new AntPathRequestMatcher(url);
			map.put(url, matcher);
		}
		return matcher;
	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();

	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public TokenBuilder getTokenBuilder() {
		return tokenBuilder;
	}

	public void setTokenBuilder(TokenBuilder tokenBuilder) {
		this.tokenBuilder = tokenBuilder;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
