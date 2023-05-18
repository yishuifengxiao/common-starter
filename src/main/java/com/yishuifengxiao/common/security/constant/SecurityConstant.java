/**
 *
 */
package com.yishuifengxiao.common.security.constant;

/**
 * 安全相关的常量类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SecurityConstant {


    /**
     * 历史请求地址
     */
    public static final String HISTORY_REQUEST_URL = "history_request_url";

    /**
     * 表单提交时 默认的用户名参数名字 username
     */
    public final static String USERNAME_PARAMETER = "username";
    /**
     * 表单提交时 默认的密码参数名字 pwd
     */
    public final static String PASSWORD_PARAMETER = "password";

    /**
     * 默认的需要删除的cookie的名字
     */
    public static final String DEFAULT_COOKIE_NAME = "JSESSIONID";


    /**
     * the HTTP Basic realm to use
     */
    public static final String REAL_NAME = "yishui";

    /**
     * 记住我产生的token
     */
    public static final String REMEMBER_ME_AUTHENTICATION_KEY = "yishuifengxiao";
    /**
     * 登陆时开启记住我的参数
     */
    public static final String REMEMBER_ME_PARAMETER = "rememberMe";
    /**
     * 短信登录参数
     */
    public static final String SMS_LOGIN_PARAM = "mobile";


}
