package com.yishuifengxiao.common.oauth2.translator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import com.yishuifengxiao.common.oauth2.Oauth2Server;
import com.yishuifengxiao.common.support.ErrorMsgUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Oauth2Server异常类型转换<br/>
 * <br/>
 * 
 * 用于对Oauth2Server中产生的异常进行异常转换<br/>
 * 
 * 该实例会被Oauth2Server收集，并通过 public void
 * configure(AuthorizationServerEndpointsConfigurer endpoints) 配置到spring
 * security中<br/>
 *
 * @see Oauth2Server
 * 
 * @author qingteng
 * @date 2020年11月2日
 * @version 1.0.0
 */
@Slf4j
public class AuthWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
	private ErrorMsgUtil errorMsgUtil;

	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Access-Control-Allow-Origin", "*");
		responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		responseHeaders.set("Access-Control-Allow-Credentials", "true");
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		responseHeaders.setAccessControlAllowCredentials(true);

		log.debug("【Oauth2服务】 Auth2认证异常，异常的原因为 {}", e);
		log.debug("【Oauth2服务】 Auth2认证异常，造成改异常的真实原因为 {}", e.getCause());

		// 获取配置的提示信息
		String msg = this.errorMsgUtil.getErrorMsg(e, "用户名或密码不正确");
		OAuth2Exception exception=new OAuth2Exception(msg);
		return new ResponseEntity<OAuth2Exception>(exception, responseHeaders, HttpStatus.OK);
	}

	public ErrorMsgUtil getErrorMsgUtil() {
		return errorMsgUtil;
	}

	public void setErrorMsgUtil(ErrorMsgUtil errorMsgUtil) {
		this.errorMsgUtil = errorMsgUtil;
	}

	public AuthWebResponseExceptionTranslator(ErrorMsgUtil errorMsgUtil) {
		this.errorMsgUtil = errorMsgUtil;
	}

	public AuthWebResponseExceptionTranslator() {

	}

}
