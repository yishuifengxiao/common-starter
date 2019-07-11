package com.yishuifengxiao.common.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.yishuifengxiao.common.security.eunm.HandleEnum;

/**
 * 请求参数提取工具类
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public class HeaderUtil {
	private final static Logger log = LoggerFactory.getLogger(HeaderUtil.class);
	
	/**
	 * 请求头CONTENT_TYPE
	 */
	public final static String CONTENT_TYPE = "Content-Type";
	
	/**
	 * http请求头名 Accept
	 */
	public final static String ACCEPT="Accept";
	
	/**
	 * json类型 json
	 */
	public final static String JSON_TYPE_KEY = "json";

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param headerName
	 *            请求头参数的名字
	 * @return 用户希望返回的数据类型
	 */
	public static HandleEnum getType(HttpServletRequest request, String headerName) {
		Assert.notNull(headerName, "提取的请求头参数的名字不能为空");
		log.debug("");
		log.debug("==start  用户请求的请求头中包含的信息为 start ===");
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
			String name = e.nextElement();
			log.debug("请求头的名字为 {},对应的值为 {}", name, request.getHeader(name));
		}
		log.debug("==end  用户请求的请求头中包含的信息为  end ===");
		log.debug("");
		// 获取到的请求数据为
		String headerValue = request.getHeader(headerName);
		HandleEnum type = HandleEnum.parse(headerValue);
		log.info("===============================> 用户希望返回的数据类型为{} ,转换后为 {}", headerValue, type);
		return type;
	}

	/**
	 * 是否使用父类的方法
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param type
	 *            系统设置的希望返回的数据类型
	 * @return
	 */
	public static boolean useDefault(HttpServletRequest request, HandleEnum type) {
		if (type == HandleEnum.DEFAULT) {
			// 获取请求头信息
			for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
				// 请求头中属性的名字
				String name = e.nextElement();
				// 查找http请求头名中名为 Accept的属性
				if (StringUtils.equalsIgnoreCase(name, ACCEPT)) {
					// 获取到请求头里的accept信息
					String accept = request.getHeader(name);
					// Accept的属性的值包含json字符串
					if (StringUtils.contains(accept.toLowerCase(), JSON_TYPE_KEY.toLowerCase())) {
						return false;
					}

				}
			}
			return true;
		}

		return false;
	}
}
