package com.yishuifengxiao.common.security.oauth2.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedClientException;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

import com.yishuifengxiao.common.tool.entity.Response;

/**
 * 自定义异常转换类 参见 参见 https://www.cnblogs.com/mxmbk/p/9782409.html
 * 
 * @author yishui
 * @date 2019年4月1日
 * @version 1.0.0
 */
public class Auth2ResponseExceptionTranslator implements WebResponseExceptionTranslator<Response<String>> {
	private final static Logger log = LoggerFactory.getLogger(Auth2ResponseExceptionTranslator.class);

	@Override
	public ResponseEntity<Response<String>> translate(Exception e) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Access-Control-Allow-Origin", "*");
		responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		responseHeaders.set("Access-Control-Allow-Credentials", "true");
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		responseHeaders.setAccessControlAllowCredentials(true);
		String msg = "token信息错误或已过期";
		log.debug("【资源服务】 Auth2异常,异常的原因为 {}", e);
		Throwable throwable = e.getCause();
		if (throwable instanceof InvalidTokenException) {
			log.info("token失效:{}", throwable);
			msg = "token已失效";
		} else if (throwable instanceof BadClientCredentialsException) {
			msg = "密码错误";
		} else if (throwable instanceof InvalidGrantException) {
			msg = "授权模方式无效";
		} else if (throwable instanceof InvalidClientException) {
			msg = "不存在对应的终端";
		} else if (throwable instanceof RedirectMismatchException) {
			msg = "请配置回调地址";
		} else if (throwable instanceof UnauthorizedClientException) {
			msg = "未获得本资源的访问授权";
		}
		return new ResponseEntity<>(Response.unAuth(msg), responseHeaders, HttpStatus.OK);
	}
}