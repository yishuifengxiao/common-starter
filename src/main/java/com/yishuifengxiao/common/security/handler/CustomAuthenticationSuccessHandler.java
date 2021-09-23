package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.yishuifengxiao.common.security.extractor.SecurityExtractor;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.resource.PropertyResource;
import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * 登陆成功处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	/**
	 * 安全处理工具
	 */
	private SecurityHelper securityHelper;

	private PropertyResource propertyResource;

	/**
	 * 信息提取器
	 */
	private SecurityExtractor securityExtractor;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		try {
			// 根据登陆信息生成一个token
			String sessionId = securityExtractor.extractUserUniqueIdentitier(request, response);

			SecurityToken token = securityHelper.createUnsafe(authentication.getName(), sessionId);
			// 登陆成功
			handlerProcessor.login(request, response, authentication, token);
		} catch (CustomException e) {
			handlerProcessor.failure(propertyResource, request, response,
					new AuthenticationServiceException(e.getMessage()));
		}

	}

	public CustomAuthenticationSuccessHandler(HandlerProcessor handlerProcessor, SecurityHelper securityHelper,
			PropertyResource propertyResource, SecurityExtractor securityExtractor) {
		this.handlerProcessor = handlerProcessor;
		this.securityHelper = securityHelper;
		this.propertyResource = propertyResource;
		this.securityExtractor = securityExtractor;
	}
	
	

}