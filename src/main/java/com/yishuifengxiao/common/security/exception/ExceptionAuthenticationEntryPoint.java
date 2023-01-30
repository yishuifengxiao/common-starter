/**
 * 
 */
package com.yishuifengxiao.common.security.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import com.yishuifengxiao.common.security.httpsecurity.authorize.impl.ExceptionAuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.impl.HttpBasicAuthorizeProvider;
import com.yishuifengxiao.common.security.httpsecurity.authorize.processor.HandlerProcessor;
import com.yishuifengxiao.common.security.support.PropertyResource;

/**
 * <p>
 * 当参数中不存在token时的提示信息 处理器
 * </p>
 * 
 * <p>
 * <strong>主要解决不存在access_token导致401的问题</strong>
 * </p>
 * 
 * 参见 https://www.cnblogs.com/mxmbk/p/9782409.html
 * 
 * 该类主要解决以下问题：
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
 * 该配置会被被两处配置收集：
 * <ul>
 * <li>在<code>ExceptionAuthorizeProvider</code>中被配置为异常处理方式</li>
 * <li>在<code>HttpBasicAuthorizeProvider</code>中被配置为异常处理方式</li>
 * <li>
 * 被<code>Oauth2Resource</code>收集，然后经<code>public void configure(ResourceServerSecurityConfigurer resources) </code>注入到oauth2中
 * </li>
 * <li>
 * 被<code>Oauth2Server</code>收集，然后经<code>public void configure(AuthorizationServerSecurityConfigurer security)</code>注入到oauth2中
 * </li>
 * </ul>
 * 
 * @see HttpBasicAuthorizeProvider
 * @see ExceptionAuthorizeProvider
 * @see Oauth2Resource
 * @see Oauth2Server
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	/**
	 * 协助处理器
	 */
	private HandlerProcessor handlerProcessor;

	private PropertyResource propertyResource;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		handlerProcessor.exception(propertyResource, request, response, authException);

	}

	public HandlerProcessor getHandlerProcessor() {
		return handlerProcessor;
	}

	public void setHandlerProcessor(HandlerProcessor handlerProcessor) {
		this.handlerProcessor = handlerProcessor;
	}

	public PropertyResource getPropertyResource() {
		return propertyResource;
	}

	public void setPropertyResource(PropertyResource propertyResource) {
		this.propertyResource = propertyResource;
	}

}
