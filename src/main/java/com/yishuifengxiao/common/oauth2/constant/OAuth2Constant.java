package com.yishuifengxiao.common.oauth2.constant;

/**
 * oauth2中client的默认参数
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OAuth2Constant {

	/**
	 * 默认的允许的认证类型
	 */
	public final static String DEFAULT_GRANT_TYPE = "password,authorization_code,refresh_token,implicit,client_credentials";
	/**
	 * token的过期时间，单位为秒，默认的token有效时间，默认为12小时
	 */
	public final static int TOKEN_VALID_TIME_IN_SECOND = 60 * 60 * 12;
	/**
	 * 默认的刷新token有效时间,默认为30天
	 */
	public final static int TOKEN_REDRESH_TIME_IN_SECOND = 60 * 60 * 24 * 30;
	/**
	 * 默认的scope
	 */
	public final static String DEFAULT_SCOPE = "read,write,trust";

	/**
	 * 默认的authorities
	 */
	public final static String DEFAULT_AUTHORTY = "ROLE_USER";
	/**
	 * 默认同意的自动授权域
	 */
	public final static String DEFAULT_APPROVE_SCOPE = "true";
	/**
	 * 默认的授权机构
	 */
	// private final static String DEFAULT_AUTHORITY =
	// "ROLE_CLIENT,ROLE_TRUSTED_CLIENT";

	public final static String DEFAULT_URL = "http://localhost:8080/";
	/**
	 * 错误码标志
	 */
	public final static String ERROR_CODE = "errcode";

}
