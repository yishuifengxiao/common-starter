package com.yishuifengxiao.common.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.AntPathMatcher;

import com.yishuifengxiao.common.constant.SessionConstant;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.event.AuthenticationSuccessEvent;
import com.yishuifengxiao.common.security.processor.ProcessHandler;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HttpUtil;

/**
 * 自定义登陆成功处理器
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月30日
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	private final static Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
	/**
	 * 获取token的地址
	 */
	private final static String AUTHORIZE_URL = "/oauth/authorize";
	/**
	 * 路径匹配策略
	 */
	private AntPathMatcher matcher = new AntPathMatcher();
	/**
	 * 重定向策略
	 */
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * 自定义属性配置
	 */
	private SecurityProperties securityProperties;

	/**
	 * 协助处理器
	 */
	private ProcessHandler customProcessor;

	private ApplicationContext context;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		// 发布事件
		context.publishEvent(new AuthenticationSuccessEvent(authentication, request));
		// 引起跳转的url
		String url = request.getRequestURI();
		String historyUrl = match(request);
		if (null != historyUrl) {
			//如果是 /oauth/authorize 请求，就直接跳转
			redirectStrategy.sendRedirect(request, response, historyUrl);
			return;
		}

		// 获取系统的处理方式
		HandleEnum handleEnum = securityProperties.getHandler().getSuc().getReturnType();

		HandleEnum type = HttpUtil.handleType(request, securityProperties.getHandler(), handleEnum);

		log.debug("【认证服务】登录成功,引起跳转的url为 {}，此登陆用户的信息为 {} ,系统配置的处理方式为 {},最终的处理方式为 {}", url, authentication, handleEnum,
				type);

		// 判断是否使用系统的默认处理方法
		if (type == HandleEnum.DEFAULT) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		customProcessor.handle(request, response, type == HandleEnum.REDIRECT,
				securityProperties.getHandler().getSuc().getRedirectUrl(),
				new Response<>(Response.Const.CODE_OK, Response.Const.MSG_OK, authentication));
	}

	/**
	 * 判断当前请求是否时符合跳转要求
	 * 
	 * @param request
	 * @return
	 */
	private String match(HttpServletRequest request) {
		// 获取到上次请求失败的url的路径
		String historyUrl = (String) request.getSession().getAttribute(SessionConstant.EXCEPTION_URL);
		if (StringUtils.isNotBlank(historyUrl)) {
			if (matcher.match(AUTHORIZE_URL, historyUrl)
					|| matcher.match(AUTHORIZE_URL, request.getContextPath() + historyUrl)) {
				// 去掉历史记录
				request.getSession().setAttribute(SessionConstant.EXCEPTION_URL, null);
				return historyUrl;
			}

		}
		return null;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public ProcessHandler getCustomHandle() {
		return customProcessor;
	}

	public void setCustomHandle(ProcessHandler customProcessor) {
		this.customProcessor = customProcessor;
	}

	public CustomAuthenticationSuccessHandler(SecurityProperties securityProperties, ProcessHandler customProcessor) {
		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
	}

	public CustomAuthenticationSuccessHandler() {

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public CustomAuthenticationSuccessHandler(SecurityProperties securityProperties, ProcessHandler customProcessor,
			ApplicationContext context) {
		this.securityProperties = securityProperties;
		this.customProcessor = customProcessor;
		this.context = context;
	}

}