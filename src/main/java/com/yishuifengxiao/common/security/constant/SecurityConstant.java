/**
 * 
 */
package com.yishuifengxiao.common.security.constant;

/**
 * 安全相关的常量类
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public final class SecurityConstant {

	/**
	 * 历史重定向地址
	 */
	public static final String HISTORY_REDIRECT_URL = "history_redirect_url";
	/**
	 * 历史请求地址
	 */
	public static final String HISTORY_REQUEST_URL = "history_request_url";

	/**
	 * 表单提交时 默认的用户名参数名字 username
	 */
	public final static String USERNAME_PARAMTER = "username";
	/**
	 * 表单提交时 默认的密码参数名字 pwd
	 */
	public final static String PASSWORD_PARAMTER = "password";

	/**
	 * 默认的需要删除的cookie的名字
	 */
	public static final String DEFAULT_COOKIE_NAME = "JSESSIONID";

	public final static class Code {
		/**
		 * 验证图片验证码时，http请求中默认的携带图片验证码信息的参数的名称
		 */
		public static final String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "imageCode";
		/**
		 * 验证短信验证码时，http请求中默认的携带短信验证码信息的参数的名称
		 */
		public static final String DEFAULT_PARAMETER_NAME_CODE_SMS = "smsCode";
		/**
		 * 发送短信验证码 或 验证短信验证码时，传递手机号的参数的名称
		 */
		public static final String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";
	}

	/**
	 * jwt加密时默认的密钥 yishui
	 */
	public static final String DEFAULT_JWT_SIGN_KEY = "yishui";

	/**
	 * 记住我产生的token
	 */
	public static final String REMEMBER_ME_AUTHENTICATION_KEY = "yishuifengxiao";
	/**
	 * 登陆时开启记住我的参数
	 */
	public static final String REMEMBER_ME_PARAMTER = "rememberMe";
	/**
	 * 短信登录参数
	 */
	public static final String SMS_LOGIN_PARAM = "mobile";
	/**
	 * basic标志
	 */
	public static final String BASIC_FLAG = "basic ";

}
