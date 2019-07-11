/**
 * 
 */
package com.yishuifengxiao.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 * 
 * @author yishui
 * @Date 2019年4月4日
 * @version 1.0.0
 */
public class StringUtil {
	/**
	 * 汉字的标志
	 */
	private final static String CHINSES_FLAG = "[\u4e00-\u9fa5]+";

	/**
	 * 如果包含汉字则返回为true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean containChinese(String str) {
		return StringUtils.isNotBlank(str) && str.matches(CHINSES_FLAG);
	}
}
