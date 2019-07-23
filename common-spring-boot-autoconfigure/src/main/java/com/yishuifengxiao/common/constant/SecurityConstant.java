/**
 * 
 */
package com.yishuifengxiao.common.constant;

/**
 * 安全相关的常量类
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public final class SecurityConstant {
	/**
	 * 表单提交时 默认的用户名参数名字 username
	 */
	public final static String USERNAME_PARAMTER = "username";
	/**
	 * 表单提交时 默认的密码参数名字 pwd
	 */
	public final static String PASSWORD_PARAMTER = "pwd";
	/**
	 * 系统默认登陆页面的地址,默认为 /login
	 */
	public final static String DEFAULT_LOGIN_URL = "/login";
	/**
	 * 权限拦截时默认的跳转地址，默认为/index
	 */
	public final static String DEFAULT_REDIRECT_LOGIN_URL = "/index";
	/**
	 * 表单登陆的地址
	 */
	public final static String DEFAULT_LOGIN_FROM_URL = "/login/form";
	/**
	 * 短信登陆的地址
	 */
	public final static String DEFAULT_LOGIN_SMS_URL = "/login/sms";
	/**
	 * 默认的获取图形验证码的地址
	 */
	public final static String DEFAULT_IMAGE_SMS_URL = "/code/image";
	/**
	 * 默认的获取短信验证码的地址
	 */
	public final static String DEFAULT_CODE_SMS_URL = "/code/sms";
	/**
	 * 默认的表单登陆时form表单请求的地址
	 */
	public final static String DEFAULT_FORM_ACTION_URL = "/auth/form";
	/**
	 * 默认的短信验证码登陆时form表单请求的地址
	 */
	public final static String DEFAULT_MOBILE_ACTION_URL = "/auth/sms";
	/**
	 * 默认的处理登陆请求的URL的路径【即请求次URL即为退出操作】
	 */
	public final static String DEFAULT_LOGINOUT_URL = "/loginOut";
	/**
	 * session失效时跳转的路径
	 */
	public final static String DEFAULT_SESSION_INVALID_URL = "/session/invalid";

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
	/**
	 * jwt加密时默认的密钥 yishui
	 */
	public static final String DEFAULT_JWT_SIGN_KEY = "yishui";

	/**
	 * 默认的需要删除的cookie的名字
	 */
	public static final String DEFAULT_COOKIE_NAME = "JSESSIONID";

	/**
	 * 默认的header名称 type
	 */
	public static final String DEFAULT_HEADER_NAME = "type";
	/**
	 * 默认的从请求参数获取处理方法的参数的名称 yishuifengxiao
	 */
	public static final String  DEFAULT_PARAM_NAME="yishuifengxiao";
	/**
	 * 默认的主页地址 /
	 */
	public static final String DEFAULT_HOME_URL = "/";

	/**
	 * 是否关闭csrf保护
	 */
	public static final Boolean CLOSE_CSRF = true;
	
	/**
	 * 是否关闭cors保护
	 */
	public static final Boolean CLOSE_CORS = false;
	/**
	 * 是否开启basic登录
	 */
	public static final Boolean HTTP_BASIC = true;
	/**
	 * 资源默认名字
	 */
	public static final String REAL_NAME = "yishuifengxiao";
	/**
	 * 记住我产生的token
	 */
	public static final String REMEMBER_ME_AUTHENTICATION_KEY="yishuifengxiao";
	/**
	 * 登陆时开启记住我的参数
	 */
	public static final String REMEMBER_ME_PARAMTER="rememberMe";
	/**
	 * 短信登录参数
	 */
	public static final String SMS_LOGIN_PARAM="mobile";

}
