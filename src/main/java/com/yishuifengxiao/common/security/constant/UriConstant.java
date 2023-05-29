/**
 *
 */
package com.yishuifengxiao.common.security.constant;

/**
 * 资源常量类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public final class UriConstant {


    /**
     * 系统默认登陆页面的地址,默认为 /security-enhance-ui
     */
    public final static String DEFAULT_LOGIN_URL = "/security-enhance-ui/";
    /**
     * 权限拦截时默认的跳转地址，默认为/index
     */
    public final static String DEFAULT_REDIRECT_LOGIN_URL = "/index";

    /**
     * 默认的表单登陆时form表单请求的地址,默认为 /web/login
     */
    public final static String DEFAULT_FORM_ACTION_URL = "/web/login";

    /**
     * 默认的处理登陆请求的URL的路径【即请求次URL即为退出操作】
     */
    public final static String DEFAULT_LOGINOUT_URL = "/loginOut";
    /**
     * session失效时跳转的路径
     */
    public final static String DEFAULT_SESSION_INVALID_URL = "/session/invalid";

    /**
     * 错误页面
     */
    public final static String ERROR_PAGE = "/error";
}
