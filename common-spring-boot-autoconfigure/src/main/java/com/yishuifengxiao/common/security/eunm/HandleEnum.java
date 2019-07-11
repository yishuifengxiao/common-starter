package com.yishuifengxiao.common.security.eunm;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义handler的数据返回类型枚举类
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public enum HandleEnum {
	/**
	 * 重定向
	 */
	REDIRECT("redirect"),
	/**
	 * 返回JSON数据
	 */
	JSON("json"),
	/**
	 * 采用内容协商的方式处理
	 */
	AUTO("auto"),
	/**
	 * 原始的处理方式
	 */
	DEFAULT("default");
	/**
	 * 处理类型
	 */
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private HandleEnum(String type) {
		this.type = type;
	}

	private HandleEnum() {
	}

	/**
	 * 将字符串转为枚举类型
	 * 
	 * @param type
	 * @return
	 */
	public static HandleEnum parse(String type) {
		if (StringUtils.isBlank(type)) {
			return null;
		}
		HandleEnum returnType = null;
		type=type.toLowerCase();
		switch (type) {
		case "redirect":
			returnType = HandleEnum.REDIRECT;
			break;
		case "json":
			returnType = HandleEnum.JSON;
			break;
		case "auto":
			returnType = HandleEnum.AUTO;
			break;
		case "default":
			returnType = HandleEnum.DEFAULT;
			break;
		default:
			break;
		}

		return returnType;
	}

}