/**
 * 
 */
package com.yishuifengxiao.common.properties.security.handler;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.security.eunm.HandleEnum;

/**
 * 登陆成功后的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class LoginSucProperties {
	/**
	 * 默认为重定向
	 */
	private HandleEnum returnType = HandleEnum.DEFAULT;
	/**
	 * 默认的重定向URL，默认为主页地址 /
	 */
	private String redirectUrl = SecurityConstant.DEFAULT_HOME_URL;

	/**
	 * 默认为重定向
	 */
	public HandleEnum getReturnType() {
		return returnType;
	}

	public void setReturnType(HandleEnum returnType) {
		this.returnType = returnType;
	}

	/**
	 * 默认的重定向URL，默认为主页地址 /
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
