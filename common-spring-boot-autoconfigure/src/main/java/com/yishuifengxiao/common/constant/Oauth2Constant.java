package com.yishuifengxiao.common.constant;

/**
 * oauth2中client的默认参数
 * 
 * @author yishui
 * @date 2019年9月29日
 * @version 1.0.0
 */
public final class Oauth2Constant {

	/**
	 * 默认的允许的认证类型
	 */
	public final static String DEFAULT_GRANT_TYPE = "password,authorization_code,refresh_token,implicit,client_credentials";
	/**
	 * 默认的token有效时间,2个小时
	 */
	public final static int TOKEN_VALID_TIME = 60 * 60 * 2;
	/**
	 * 默认的刷新token有效时间,默认为24小时
	 */
	public final static int TOKEN_REDRESH_TIME = 60 * 60 * 24;
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

}
