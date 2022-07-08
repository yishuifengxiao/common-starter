package com.yishuifengxiao.common.security.filter.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.yishuifengxiao.common.security.extractor.SecurityTokenExtractor;
import com.yishuifengxiao.common.security.filter.SecurityRequestFilter;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 校验token的合法性
 * </p>
 * 
 * <p>
 * 即判断用户请求里携带的访问令牌是否为合法且在有效状态 ，同时判断一下该用户的账号的状态
 * </p>
 * 
 * <p>
 * 用于在非oauth2的情况下，在仅仅使用spring Security时系统从用户提供的请求里解析出认证信息，判断用户是否能够认证
 * </p>
 * 在此情况下，除了忽视资源和非管理资源不需要经过该逻辑，理论上一版情况下其他资源都要经过该逻辑
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TokenValidateFilter extends SecurityRequestFilter implements InitializingBean {

	private Map<String, AntPathRequestMatcher> map = new HashMap<>();

	/**
	 * 是否显示加载日志
	 */
	private boolean show = false;

	private PropertyResource propertyResource;

	private HandlerProcessor handlerProcessor;

	private SecurityTokenExtractor securityTokenExtractor;

	private SecurityHelper securityHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// 先判断请求是否需要经过授权校验
		boolean requiresAuthentication = this.requiresAuth(request);
		if (show) {
			log.info("【易水组件】请求 {} 是否需要进行校验校验的结果为 {}", request.getRequestURI(), requiresAuthentication);
		}

		if (propertyResource.security().isOpenTokenFilter() && requiresAuthentication) {

			try {
				// 从请求中获取到携带的认证
				String tokenValue = securityTokenExtractor.extractTokenValue(request, response, propertyResource);

				if (show) {
					log.info("【易水组件】请求 {} 携带的认证信息为 {}", request.getRequestURI(), tokenValue);
				}

				if (StringUtils.isNotBlank(tokenValue)) {
					// 该请求携带了认证信息
					Authentication authentication = securityHelper.authorize(tokenValue);
					// 将认证信息注入到spring Security中
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}

			} catch (CustomException e) {
				handlerProcessor.preAuth(request, response, Response
						.of(propertyResource.security().getMsg().getInvalidTokenValueCode(), e.getMessage(), e));
				return;
			} catch (Exception e) {
				handlerProcessor.exception(propertyResource, request, response, e);
				return;
			}

		}
		filterChain.doFilter(request, response);
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
			if (show) {
				log.info("【易水组件】判断请求是否需要授权认证时出现问题，出现问题的原因为 {}", e.getMessage());
			}
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
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(this, LogoutFilter.class);

	}

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();

	}

	public TokenValidateFilter(PropertyResource propertyResource, HandlerProcessor handlerProcessor,
			SecurityTokenExtractor securityTokenExtractor, SecurityHelper securityHelper) {
		this.propertyResource = propertyResource;
		this.handlerProcessor = handlerProcessor;
		this.securityTokenExtractor = securityTokenExtractor;
		this.securityHelper = securityHelper;
		this.show = propertyResource.security().getShowDeatil();

	}

}
