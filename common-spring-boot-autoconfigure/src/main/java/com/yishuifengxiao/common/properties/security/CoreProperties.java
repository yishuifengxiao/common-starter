package com.yishuifengxiao.common.properties.security;

import com.yishuifengxiao.common.constant.SecurityConstant;

/**
 * spring security 核心配置文件类
 * 
 * @version 0.0.1
 * @author yishui
 * @date 2018年6月29日
 */
public class CoreProperties {
	/**
	 * 表单提交时默认的用户名参数
	 */
	private String usernameParameter = SecurityConstant.USERNAME_PARAMTER;
	/**
	 * 表单提交时默认的密码名参数
	 */
	private String passwordParameter = SecurityConstant.PASSWORD_PARAMTER;
	/**
	 * 系统登陆页面的地址 ,默认为 /login
	 */
	private String loginPage = SecurityConstant.DEFAULT_LOGIN_URL;
	/**
	 * 权限拦截时默认的跳转地址，默认为 /default
	 */
	private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;
	/**
	 * 表单登陆时form表单请求的地址，默认为/auth/form
	 */
	private String formActionUrl = SecurityConstant.DEFAULT_FORM_ACTION_URL;


	/**
	 * 默认的处理登出请求的URL的路径【即请求此URL即为退出操作】，默认为/loginOut
	 */
	private String loginOutUrl = SecurityConstant.DEFAULT_LOGINOUT_URL;

	/**
	 * 系统登陆页面的地址，默认为/login
	 */
	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}

	/**
	 * 权限拦截时默认的跳转地址，默认为/default
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	/**
	 * 表单登陆时form表单请求的地址，默认为/auth/form
	 */
	public String getFormActionUrl() {
		return formActionUrl;
	}

	public void setFormActionUrl(String formActionUrl) {
		this.formActionUrl = formActionUrl;
	}

	/**
	 * 默认的处理登出请求的URL的路径【即请求次URL即为退出操作】，默认为/login/loginOut
	 */
	public String getLoginOutUrl() {
		return loginOutUrl;
	}

	public void setLoginOutUrl(String loginOutUrl) {
		this.loginOutUrl = loginOutUrl;
	}

	/**
	 * 表单提交时默认的用户名参数
	 */
	public String getUsernameParameter() {
		return usernameParameter;
	}

	public void setUsernameParameter(String usernameParameter) {
		this.usernameParameter = usernameParameter;
	}

	/**
	 * 表单提交时默认的密码名参数
	 */
	public String getPasswordParameter() {
		return passwordParameter;
	}

	public void setPasswordParameter(String passwordParameter) {
		this.passwordParameter = passwordParameter;
	}

}