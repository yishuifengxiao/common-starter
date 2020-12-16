package com.yishuifengxiao.common.oauth2.translator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import com.yishuifengxiao.common.support.ErrorMsgUtil;
import com.yishuifengxiao.common.tool.entity.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常转换类 <br/>
 * <strong>主要解决失效的token导致401的问题</strong><br/>
 * 
 * <pre>
 *  传递失效access_token，返回401状态，期望是200同时以错误码方式提示token失效。

      排查：经过单步调试分析源码发现，token失效后，认证服务器会抛出异常，同时响应给资源服务器，资源服务发现认证服务器的错误后会抛出InvalideException。

                抛出的异常会经过默认的DefaultWebResponseExceptionTranslator 处理然后 Reseponse给Client端。

      解决：通过上面的分析指导。最后的异常是在DefaultWebResponseExceptionTranslator 处理的，所以只需要

   自定义实现类Implements WebResponseExceptionTranslator 接口处理异常装换逻辑，
   使得自定义的类生效
 * 
 * 
 * </pre>
 * 
 * 
 * 该类型会被<code>Oauth2Resource</code>收集，经过<code>public void configure(ResourceServerSecurityConfigurer resources)</code>注入到oauth2中<br/>
 * 经过 参见 https://www.cnblogs.com/mxmbk/p/9782409.html
 * 
 * @see Oauth2Resource
 * @author yishui
 * @date 2019年4月1日
 * @version 1.0.0
 */
@Slf4j
public class Auth2ResponseExceptionTranslator implements WebResponseExceptionTranslator<Response<Object>> {

	private ErrorMsgUtil errorMsgUtil;

	@Override
	public ResponseEntity<Response<Object>> translate(Exception e) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Access-Control-Allow-Origin", "*");
		responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		responseHeaders.set("Access-Control-Allow-Credentials", "true");
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		responseHeaders.setAccessControlAllowCredentials(true);

		log.debug("【资源服务】 Auth2异常，异常的原因为 {}", e);
		log.debug("【资源服务】 Auth2异常，造成改异常的真实原因为 {}", e.getCause());

		// 获取配置的提示信息
		String msg = this.errorMsgUtil.getErrorMsg(e.getCause(), "token信息错误或已过期");
		return new ResponseEntity<>(Response.unAuth(msg).setData(e), responseHeaders, HttpStatus.OK);
	}

	public Auth2ResponseExceptionTranslator() {

	}

	public Auth2ResponseExceptionTranslator(ErrorMsgUtil errorMsgUtil) {
		this.errorMsgUtil = errorMsgUtil;
	}

	public ErrorMsgUtil getErrorMsgUtil() {
		return errorMsgUtil;
	}

	public void setErrorMsgUtil(ErrorMsgUtil errorMsgUtil) {
		this.errorMsgUtil = errorMsgUtil;
	}

}