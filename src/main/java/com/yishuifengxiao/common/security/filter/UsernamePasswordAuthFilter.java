package com.yishuifengxiao.common.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.httpsecurity.impl.UsernameExtisAuthInterceptor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * 登陆时用户名和密码校验<br/>
 * <br/>
 * 用于在UsernamePasswordAuthenticationFilter
 * 之前提前校验一下用户名是否已经存在,会在UsernameAuthInterceptor中被收集注入
 * 
 * @see UsernamePasswordAuthenticationFilter
 * @see UsernameExtisAuthInterceptor
 * @author qingteng
 * @date 2020年11月24日
 * @version 1.0.0
 */
@Slf4j
public class UsernamePasswordAuthFilter extends OncePerRequestFilter {

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	private AntPathRequestMatcher pathMatcher = null;
	/**
	 * 配置属性
	 */
	private SecurityProperties securityProperties;

	private HandlerProcessor handlerProcessor;

	private UserDetailsService userDetailsService;

	private PasswordEncoder passwordEncoder;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 是否关闭前置参数校验功能
		Boolean closePreAuth = securityProperties.getCore().getClosePreAuth();
		if (BooleanUtils.isNotTrue(closePreAuth)) {
			AntPathRequestMatcher pathMatcher = this.antPathMatcher();
			if (pathMatcher.matches(request)) {

				String username = obtainUsername(request);
				String password = obtainPassword(request);

				if (username == null) {
					username = "";
				}
				if (password == null) {
					password = "";
				}

				username = username.trim();

				try {
					// 账号密码检查
					check(username, password);
				} catch (CustomException exception) {
					handlerProcessor.preAuth(request, response,
							Response.of(Response.Const.CODE_INTERNAL_SERVER_ERROR, exception.getMessage(), exception));
					return;
				} catch (Exception e) {
					handlerProcessor.exception(request, response, e);
					log.debug("【易水组件】校验用户名时出现问题，出现问题的原因为 {}", e.getMessage());
					return;
				}

			}
		}

		filterChain.doFilter(request, response);
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

	/**
	 * 获取用户名
	 * 
	 * @param request
	 * @return
	 */
	private String obtainUsername(HttpServletRequest request) {
		String usernameParameter = securityProperties.getCore().getUsernameParameter();
		if (StringUtils.isBlank(usernameParameter)) {
			usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
		}
		return request.getParameter(usernameParameter.trim());
	}

	/**
	 * 获取密码
	 * 
	 * @param request
	 * @return
	 */
	private String obtainPassword(HttpServletRequest request) {
		String passwordParameter = securityProperties.getCore().getPasswordParameter();
		if (StringUtils.isBlank(passwordParameter)) {
			passwordParameter = SPRING_SECURITY_FORM_PASSWORD_KEY;
		}
		return request.getParameter(passwordParameter.trim());
	}

	/**
	 * 获取到路径匹配器
	 * 
	 * @return 路径匹配器
	 */
	private AntPathRequestMatcher antPathMatcher() {
		if (null == this.pathMatcher) {
			this.pathMatcher = new AntPathRequestMatcher(this.securityProperties.getCore().getFormActionUrl());
		}
		return this.pathMatcher;
	}

	public AntPathRequestMatcher getPathMatcher() {
		return pathMatcher;
	}

	public void setPathMatcher(AntPathRequestMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

}
