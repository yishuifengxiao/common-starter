/**
 * 
 */
package com.yishuifengxiao.common.security.constant;

/**
 * OAuth2相关的常量类
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public final class OAuth2Constant {

	/**
	 * 存储token的session的key值
	 */
	public static final String SESSION_TOKEN_KEY = "yishuifengxiao.session.token";
	/**
	 * oauth中token地址
	 */
	public static final String OAUTH_TOKEN = "/oauth/token";

	/**
	 * oauth中检查token地址
	 */
	public static final String OAUTH_CHECK_TOKEN = "/oauth/check_token";

	/**
	 * 资源默认名字
	 */
	public static final String REAL_NAME = "yishuifengxiao";
	
	
	/**
	 * 获取token的地址
	 */
	public final static String AUTHORIZE_URL = "/oauth/authorize";

}
