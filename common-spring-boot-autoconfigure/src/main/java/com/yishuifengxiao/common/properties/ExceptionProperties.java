package com.yishuifengxiao.common.properties;

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
	 * 异常类型存储 <br/>
	 * 键：异常类型的名字，如 ConstraintViolationException <br/>
	 * 值：提示信息
	 */
	private Map<String, String> map = new HashMap<>();

	public ExceptionProperties() {

		map.put("ConstraintViolationException", "已经存在相似的数据,不能重复添加");

		map.put("DataIntegrityViolationException", "已经存在相似的数据,不能重复添加");

		map.put("DuplicateKeyException", "已经存在相似的数据,不能重复添加");

	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

}
