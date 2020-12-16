package com.yishuifengxiao.common.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常信息提示配置
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
@ConfigurationProperties(prefix = "yishuifengxiao.error")
public class ExceptionProperties {
	/**
	 * 是否开启全局异常拦截功能，默认为开启
	 */
	private Boolean enable = true;
	/**
	 * 简单异常提示信息存储 <br/>
	 * key：异常类型的名字，如 ConstraintViolationException <br/>
	 * value：提示信息
	 */
	private Map<String, String> map = new HashMap<>();
	/**
	 * 完整异常信息提示<br/>
	 * key：异常类型的完整名字，如 com.yishuifengxiao.common.tool.exception.ServiceException
	 * <br/>
	 * value：提示信息，例如 用户信息不能为空
	 */
	private Map<String, String> full = new HashMap<>();

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public Map<String, String> getFull() {
		return full;
	}

	public void setFull(Map<String, String> full) {
		this.full = full;
	}

}
