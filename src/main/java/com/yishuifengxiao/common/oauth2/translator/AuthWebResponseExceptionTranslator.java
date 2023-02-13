package com.yishuifengxiao.common.oauth2.translator;

import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.web.error.ErrorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * <p>
 * Oauth2Server异常类型转换
 * </p>
 * <p>
 * <p>
 * 用于对Oauth2Server中产生的异常进行异常转换,
 * <p>
 * 该实例会被Oauth2Server收集，并通过 public void
 * configure(AuthorizationServerEndpointsConfigurer endpoints) 配置到spring
 * security中
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
@Slf4j
public class AuthWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

	private final ErrorHelper errorHelper;

	@SuppressWarnings({ "unused", "rawtypes" })
	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Access-Control-Allow-Origin", "*");
		responseHeaders.set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		responseHeaders.set("Access-Control-Allow-Credentials", "true");
		responseHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		responseHeaders.setAccessControlAllowCredentials(true);

		log.debug("【Oauth2服务】 Auth2认证异常，异常的原因为 {}", e);

		// 获取配置的提示信息
		Object extract = errorHelper.extract(e);
		if (null == extract) {
			extract = "未知异常";
		}
		OAuth2Exception exception = new OAuth2Exception(
				(extract instanceof Response) ? ((Response) extract).getMsg() : extract.toString());
		return new ResponseEntity<OAuth2Exception>(exception, responseHeaders, HttpStatus.OK);
	}

	public AuthWebResponseExceptionTranslator(ErrorHelper errorHelper) {
		this.errorHelper = errorHelper;
	}

}
