package com.yishuifengxiao.common.properties.security;

import java.util.HashMap;
import java.util.Map;

import com.yishuifengxiao.common.constant.SecurityConstant;

/**
 * 自定义属性配置文件
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class ValidateProperties {

	/**
	 * 是否过滤GET方法，默认为false
	 */
	private Boolean isFilterGet = false;
	/**
	 * 短信登陆参数,默认为 mobile
	 */
	private String smsLoginParam = SecurityConstant.SMS_LOGIN_PARAM;
	/**
	 * 短信验证码登录地址
	 */
	private String smsLoginUrl;
	/**
	 * 需要过滤的路径<br/>
	 * key：验证码类型的名字<br/>
	 * value: 需要过滤的路径，多个路径采用半角的逗号分隔
	 */
	private Map<String, String> filter = new HashMap<>();

	public Map<String, String> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, String> filter) {
		this.filter = filter;
	}

	/**
	 * 短信验证码登录地址
	 * 
	 * @return
	 */
	public String getSmsLoginUrl() {
		return smsLoginUrl;
	}

	public void setSmsLoginUrl(String smsLoginUrl) {
		this.smsLoginUrl = smsLoginUrl;
	}

	/**
	 * 是否过滤GET方法，默认为false
	 * 
	 * @return
	 */
	public Boolean getIsFilterGet() {
		return isFilterGet;
	}

	public void setIsFilterGet(Boolean isFilterGet) {
		this.isFilterGet = isFilterGet;
	}

	/**
	 * 短信登陆参数,默认为 mobile
	 * 
	 * @return
	 */
	public String getSmsLoginParam() {
		return smsLoginParam;
	}

	public void setSmsLoginParam(String smsLoginParam) {
		this.smsLoginParam = smsLoginParam;
	}

}