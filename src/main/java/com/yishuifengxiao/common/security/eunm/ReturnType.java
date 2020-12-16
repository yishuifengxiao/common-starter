package com.yishuifengxiao.common.security.eunm;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义handler的数据返回类型枚举类
 * 
 * @author yishui
 * @date 2019年1月5日
 * @version 0.0.1
 */
public enum ReturnType {
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
	AUTO("auto");
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

	private ReturnType(String type) {
		this.type = type;
	}

	private ReturnType() {
	}

	/**
	 * 将字符串转为枚举类型
	 * 
	 * @param type
	 * @return
	 */
	public static ReturnType parse(String type) {
		if (StringUtils.isBlank(type)) {
			return null;
		}
		ReturnType returnType = null;
		type = type.toLowerCase();
		switch (type) {
		case "redirect":
			returnType = ReturnType.REDIRECT;
			break;
		case "json":
			returnType = ReturnType.JSON;
			break;
		case "auto":
			returnType = ReturnType.AUTO;
			break;
		default:
			break;
		}

		return returnType;
	}

}