/**
 * 
 */
package com.yishuifengxiao.common.properties.security.handler;

import com.yishuifengxiao.common.constant.SecurityConstant;
import com.yishuifengxiao.common.security.eunm.HandleEnum;

/**
 * 权限拒绝时的配置
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public class AccessDenieProperties {
	/**
	 * 默认为内容协商
	 */
	private HandleEnum returnType = HandleEnum.AUTO;
	/**
	 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
	 */
	private String redirectUrl = SecurityConstant.DEFAULT_REDIRECT_LOGIN_URL;

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
	 * 默认的重定向URL，若该值为空，则跳转到权限拦截的页面
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
