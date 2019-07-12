/**
 * 
 */
package com.yishuifengxiao.common.properties.security;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.properties.security.handler.AccessDenieProperties;
import com.yishuifengxiao.common.properties.security.handler.ExceptionProperties;
import com.yishuifengxiao.common.properties.security.handler.LoginFailProperties;
import com.yishuifengxiao.common.properties.security.handler.LoginOutProperties;
import com.yishuifengxiao.common.properties.security.handler.LoginSucProperties;

/**
 * 自定义handler的配置文件
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class HandlerProperties {
	/**
	 * 登陆成功后的处理方式
	 */
	private LoginSucProperties suc = new LoginSucProperties();
	/**
	 * 登陆失败时的处理方式
	 */
	private LoginFailProperties fail = new LoginFailProperties();
	/**
	 * 退出成功后的配置
	 */
	private LoginOutProperties exit = new LoginOutProperties();

	/**
	 * 出现异常时的配置
	 */
	private ExceptionProperties exception = new ExceptionProperties();
	/**
	 * 权限拒绝时的配置
	 */
	private AccessDenieProperties denie = new AccessDenieProperties();
	/**
	 * 默认的header名称 type
	 */
	private String headerName = SecurityConstant.DEFAULT_HEADER_NAME;
	/**
	 * 默认的从请求参数获取处理方法的参数的名称 yishuifengxiao
	 */
	private String paramName = SecurityConstant.DEFAULT_PARAM_NAME;

	/**
	 * 登陆成功后的处理方式
	 */
	public LoginSucProperties getSuc() {
		return suc;
	}

	public void setSuc(LoginSucProperties suc) {
		this.suc = suc;
	}

	/**
	 * 登陆失败时的处理方式
	 */
	public LoginFailProperties getFail() {
		return fail;
	}

	public void setFail(LoginFailProperties fail) {
		this.fail = fail;
	}

	/**
	 * 退出成功后的配置
	 */
	public LoginOutProperties getExit() {
		return exit;
	}

	public void setExit(LoginOutProperties exit) {
		this.exit = exit;
	}

	/**
	 * 默认的header名称 type
	 */
	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	/**
	 * 出现异常时的配置
	 */
	public ExceptionProperties getException() {
		return exception;
	}

	public void setException(ExceptionProperties exception) {
		this.exception = exception;
	}

	/**
	 * 权限拒绝时的配置
	 */
	public AccessDenieProperties getDenie() {
		return denie;
	}

	public void setDenie(AccessDenieProperties denie) {
		this.denie = denie;
	}

	/**
	 * 默认的从请求参数获取处理方法的参数的名称 yishuifengxiao
	 * 
	 * @return
	 */
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

}
