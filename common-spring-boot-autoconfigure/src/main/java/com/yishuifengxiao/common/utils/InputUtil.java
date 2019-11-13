package com.yishuifengxiao.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 输入参数处理工具类
 * 
 * @author yishui
 * @date 2019年11月13日
 * @version 1.0.0
 */
public class InputUtil {

	/**
	 * 对传入的参数进行非空处理 <br/>
	 * 当传入的参数为 null 或 "" 或 "undefined" 直接返回为null,否则返回原始值
	 * 
	 * @param t 传入的参数
	 * @return 处理后的参数
	 */
	public static <T> T convert(T t) {
		return t == null || "".equals(t) || "undefined".equals(t) ? null : t;
	}

	/**
	 * 对字符串进行非空和空格处理
	 * 
	 * @param str 传入的参数
	 * @return 处理后的参数
	 */
	public static  String trim(String str) {
		return StringUtils.isNotBlank(str) ? str.trim() : null;
	}

	/**
	 * 对参数进行非空和空格处理，并对undefined值的数据进行过滤
	 * 
	 * @param str
	 * @return
	 */
	public static  String undefined(String str) {
		str = trim(str);
		return StringUtils.equalsIgnoreCase(str, "undefined") ? null : str;
	}

	/**
	 * 将字符串转为Double
	 * 
	 * @param str
	 * @return
	 */
	public static  Double convert2Double(String str) {
		if (StringUtils.isNumeric(str)) {
			return Double.parseDouble(str);
		}
		return null;
	}

	/**
	 * 将字符串转为 Long
	 * 
	 * @param str
	 * @return
	 */
	public static  Long convert2Long(String str) {
		if (StringUtils.isNumeric(str)) {
			return Long.parseLong(str);
		}
		return null;
	}

	/**
	 * 将字符串的首字母变为小写的
	 * 
	 * @param s 字符串
	 * @return
	 */
	public static  String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

}
