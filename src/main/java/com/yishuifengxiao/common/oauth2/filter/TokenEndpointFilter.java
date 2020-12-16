/**
 * 
 */
package com.yishuifengxiao.common.oauth2.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * 用于oauth2密码模式下载提前校验用户名和密码
 * 
 * @author qingteng
 * @date 2020年11月23日
 * @version 1.0.0
 */
public class TokenEndpointFilter implements Filter {

	private static final AntPathRequestMatcher MATCHER = new AntPathRequestMatcher("/oauth/token");

	private static final String USERNAME = "username";

	private static final String PASSWORD = "password";

	private static final String GRANT_TYPE = "grant_type";

	private static final String PARAM_VALUE = "password";

	private UserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder;
	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (MATCHER.matches(httpServletRequest)) {
			// 授权类型
			String grantType = httpServletRequest.getParameter(GRANT_TYPE);
			if (StringUtils.containsIgnoreCase(PARAM_VALUE, grantType)) {
				// 密码模式

				String username = httpServletRequest.getParameter(USERNAME);
				String password = httpServletRequest.getParameter(PASSWORD);

				try {
					if (StringUtils.isBlank(username)) {
						throw new CustomException(ErrorCode.USERNAME_NULL, "用户名不能为空");
					}

					if (StringUtils.isBlank(password)) {
						throw new CustomException(ErrorCode.PASSWORD_NULL, "密码不能为空");
					}
					// 账号密码检查
					check(username, password);
				} catch (CustomException exception) {
					handlerProcessor.preAuth(httpServletRequest, httpServletResponse,
							Response.of(Response.Const.CODE_INTERNAL_SERVER_ERROR, exception.getMessage(), exception));
					return;
				}

				catch (Exception exception) {
					handlerProcessor.exception(httpServletRequest, httpServletResponse, exception);
					return;
				}

			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * 账号密码检查
	 * 
	 * @param username 用户名
	 * @param password 密码
	 * @throws CustomException
	 */
	private void check(String username, String password) throws CustomException {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (null == userDetails) {
			throw new CustomException(ErrorCode.USERNAME_NO_EXTIS, "用户名不存在");
		}
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new CustomException(ErrorCode.PASSWORD_ERROR, "密码错误");
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

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

}
