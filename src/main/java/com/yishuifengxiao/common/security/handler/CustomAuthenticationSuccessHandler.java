package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.AntPathMatcher;

import com.yishuifengxiao.common.security.SecurityProperties;
import com.yishuifengxiao.common.security.constant.OAuth2Constant;
import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.event.AuthenticationSuccessEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.builder.TokenBuilder;
import com.yishuifengxiao.common.support.SpringContext;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.random.UID;

/**
 * 登陆成功处理器
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月30日
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	/**
	 * 路径匹配策略
	 */
	private final AntPathMatcher matcher = new AntPathMatcher();
	/**
	 * 重定向策略
	 */
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	/**
	 * token生成器
	 */
	private TokenBuilder tokenBuilder;

	private SecurityProperties securityProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 发布事件
		SpringContext.publishEvent(new AuthenticationSuccessEvent(this, request, authentication));
		String historyUrl = match(request);
		if (StringUtils.isNotBlank(historyUrl)) {
			// 如果是 /oauth/authorize 请求，就直接跳转
			redirectStrategy.sendRedirect(request, response, historyUrl);
			return;
		}
		String sessionId = this.getUserUniqueIdentitier(request);
		try {
			// 根据登陆信息生成一个token
			SecurityToken token = tokenBuilder.creatNewToken(authentication.getName(), sessionId,
					securityProperties.getToken().getValidSeconds(), securityProperties.getToken().getPreventsLogin(),
					securityProperties.getToken().getMaxSessions());
			// 登陆成功
			handlerProcessor.login(request, response, authentication, token);
		} catch (CustomException e) {
			handlerProcessor.failure(request, response, new AuthenticationServiceException(e.getMessage()));
		}

	}

	/**
	 * 从请求中获取用户的唯一标识符
	 * 
	 * @param request
	 * @return 用户的唯一标识符
	 */
	private String getUserUniqueIdentitier(HttpServletRequest request) {
		String identitierParamter = securityProperties.getToken().getUserUniqueIdentitier();
		if (StringUtils.isBlank(identitierParamter)) {
			identitierParamter = TokenConstant.USER_UNIQUE_IDENTIFIER;
		}
		String identitierValue = request.getHeader(identitierParamter);
		if (StringUtils.isBlank(identitierValue)) {
			identitierValue = request.getParameter(identitierParamter);
		}
		if (StringUtils.isBlank(identitierValue)) {
			identitierValue = request.getSession().getId();
		}
		if (StringUtils.isBlank(identitierValue)) {
			identitierValue = UID.uuid();
		}
		return identitierValue;
	}

	/**
	 * 判断当前请求是否时符合跳转要求
	 * 
	 * @param request
	 * @return
	 */
	private String match(HttpServletRequest request) {

		String historyUrl = (String) request.getSession().getAttribute(SecurityConstant.HISTORY_REDIRECT_URL);
		if (matcher.match(OAuth2Constant.AUTHORIZE_URL, historyUrl)) {
			return historyUrl;
		}

		historyUrl = (String) request.getSession().getAttribute(SecurityConstant.HISTORY_REQUEST_URL);
		if (matcher.match(OAuth2Constant.AUTHORIZE_URL, historyUrl)) {
			return historyUrl;
		}
		return null;
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

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

}