/**
 * 
 */
package com.yishuifengxiao.common.security.endpoint;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.yishuifengxiao.common.oauth2.Oauth2Resource;
import com.yishuifengxiao.common.oauth2.Oauth2Server;
import com.yishuifengxiao.common.security.constant.SecurityConstant;
import com.yishuifengxiao.common.security.event.ExceptionAuthenticationEntryPointEvent;
import com.yishuifengxiao.common.security.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.provider.impl.ExceptionAuthorizeProvider;
import com.yishuifengxiao.common.security.provider.impl.HttpBasicAuthorizeProvider;
import com.yishuifengxiao.common.support.SpringContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 当参数中不存在token时的提示信息 处理器<br/>
 * 
 * <strong>主要解决不存在access_token导致401的问题</strong><br/>
 * 
 * 参见 https://www.cnblogs.com/mxmbk/p/9782409.html<br/>
 * 
 * 该类主要解决以下问题：<br/>
 * 
 * <pre>
 * 
  * 问题：测试发现授权接口，当请求参数中不存在access_token时发现接口返回错误信息：
 *               {"timestamp":1539337154336,"status":401,"error":"Unauthorized","message":"No message available","path":"/app/businessCode/list"}
 * 
   *   排查：经过前面的分析发现，上面提到Security的FilterSecurityInterceptor对OAuth2中返回的信息和本身配置校验后，抛出AccessDenyException。
 * 
   *  解决：经过上面的几个问题的处理，发现思路还是一样的，需要定义响应结果，
 * 
  *                 即1、自定义响应处理逻辑SecurityAuthenticationEntryPoint 2、自定义处理逻辑SecurityAuthenticationEntryPoint生效（见上面的配置）
 * 
 * </pre>
 * 
 * 该配置会被被两处配置收集：<br/>
 * 1 在<code>ExceptionAuthorizeProvider</code>中被配置为异常处理方式<br/>
 * 2 在<code>HttpBasicAuthorizeProvider</code>中被配置为异常处理方式<br/>
 * 3
 * 被<code>Oauth2Resource</code>收集，然后经<code>public void configure(ResourceServerSecurityConfigurer resources) </code>注入到oauth2中<br/>
 * 4
 * 被<code>Oauth2Server</code>收集，然后经<code>public void configure(AuthorizationServerSecurityConfigurer security)</code>注入到oauth2中
 * 
 * @see HttpBasicAuthorizeProvider
 * @see ExceptionAuthorizeProvider
 * @see Oauth2Resource
 * @see Oauth2Server
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
@Slf4j
public class ExceptionAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	/**
	 * 声明了缓存与恢复操作
	 */
	private RequestCache cache = new HttpSessionRequestCache();

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		// 发布信息
		SpringContext.publishEvent(new ExceptionAuthenticationEntryPointEvent(this, request, authException));
		// 引起跳转的uri
		SavedRequest savedRequest = cache.getRequest(request, response);
		String url = savedRequest != null ? savedRequest.getRedirectUrl() : request.getRequestURL().toString();

		request.getSession().setAttribute(SecurityConstant.HISTORY_REDIRECT_URL, savedRequest.getRedirectUrl());
		request.getSession().setAttribute(SecurityConstant.HISTORY_REQUEST_URL, request.getRequestURL().toString());

		log.debug("【易水组件】获取资源 失败(可能是缺少token),该资源的url为 {} ,失败的原因为 {}", url, authException);

		handlerProcessor.exception(request, response, authException);

	}

	public RequestCache getCache() {
		return cache;
	}

	public void setCache(RequestCache cache) {
		this.cache = cache;
	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

}
