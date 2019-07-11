package com.yishuifengxiao.common.properties.security;

import com.yishuifengxiao.common.constant.SecurityConstant;

/**
 * 记住我相关的属性配置
 * 
 * @author yishui
 * @date 2019年1月8日
 * @version 0.0.1
 */
public class RemeberMeProperties {
	/**
	 * 是否使用安全cookie
	 */
	private Boolean useSecureCookie=true;
	/**
	 * 记住我产生的token，默认为 yishuifengxiao
	 */
	private String key=SecurityConstant.REMEMBER_ME_AUTHENTICATION_KEY;
	/**
	 * 登陆时开启记住我的参数,默认为 rememberMe
	 */
	private String rememberMeParameter=SecurityConstant.REMEMBER_ME_PARAMTER;
	
	/**
	 * 默认过期时间为60分钟
	 */
	private Integer rememberMeSeconds = 60 * 60;

	public Boolean getUseSecureCookie() {
		return useSecureCookie;
	}

	public void setUseSecureCookie(Boolean useSecureCookie) {
		this.useSecureCookie = useSecureCookie;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRememberMeParameter() {
		return rememberMeParameter;
	}

	public void setRememberMeParameter(String rememberMeParameter) {
		this.rememberMeParameter = rememberMeParameter;
	}
	

	/**
	 * 默认过期时间为60分钟
	 */
	public Integer getRememberMeSeconds() {
		return rememberMeSeconds;
	}

	public void setRememberMeSeconds(Integer rememberMeSeconds) {
		this.rememberMeSeconds = rememberMeSeconds;
	}


}
