/**
 * 
 */
package com.yishuifengxiao.common.utils;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author yishui
 * @Date 2019年4月4日
 * @version 1.0.0
 */
public class RegexUtil {
	/**
	 * 包含中文的正则
	 */
	private final static Pattern CHINESE_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

	/**
	 * 如果包含汉字则返回为true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containChinese(String str) {
		return CHINESE_PATTERN.matcher(str).matches();
	}
}
