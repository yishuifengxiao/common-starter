/**
 * 
 */
package com.yishuifengxiao.common.properties.security.handler;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.security.eunm.HandleEnum;

/**
 * 登陆失败后的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class LoginFailProperties {
	/**
	 * 默认为内容协商处理
	 */
	private HandleEnum returnType = HandleEnum.AUTO;
	/**
	 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
	 */
	private String redirectUrl=SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

	/**
	 * 默认为内容协商处理
	 */
	public HandleEnum getReturnType() {
		return returnType;
	}

	public void setReturnType(HandleEnum returnType) {
		this.returnType = returnType;
	}
	
	
	/**
	 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面 /index
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
