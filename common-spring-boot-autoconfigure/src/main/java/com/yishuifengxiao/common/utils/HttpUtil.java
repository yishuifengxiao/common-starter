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
public class HttpUtil {
	private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * 请求头CONTENT_TYPE
	 */
	public final static String CONTENT_TYPE = "Content-Type";

	/**
	 * http请求头名 Accept
	 */
	public final static String ACCEPT = "Accept";

	/**
	 * json类型 json
	 */
	public final static String JSON_TYPE_KEY = "json";

	/**
	 * 
	 * @param request    HttpServletRequest
	 * @param headerName 请求头参数的名字
	 * @return 用户希望返回的数据类型
	 */
	public static void stack(HttpServletRequest request) {
		log.debug("");
		log.debug("==start  用户请求的请求头中包含的信息为 start ===");
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
			String name = e.nextElement();
			log.debug("请求头的名字为 {},对应的值为 {}", name, request.getHeader(name));
		}
		log.debug("==end  用户请求的请求头中包含的信息为  end ===");
		log.debug("");
	}

	/**
	 * 从请求中获取处理方式
	 * 
	 * @param request HttpServletRequest
	 * @param value   请求获取参数
	 */
	public static HandleEnum handleType(HttpServletRequest request, String name) {
		Assert.notNull(name, "请求头参数不能为空");
		// 打印请求头
		stack(request);
		HandleEnum handleType = null;
		// 获取对应的请求头参数的值
		handleType = HandleEnum.parse(request.getHeader(name));
		// 从ACCEPT参数中获取
		if (handleType == null && StringUtils.containsIgnoreCase(request.getHeader(ACCEPT), JSON_TYPE_KEY)) {
			handleType = HandleEnum.JSON;
		}
		// 在特定的请求头未获取到参数
		if (handleType == null) {
			handleType = HandleEnum.parse(request.getParameter(name));
		}

		return handleType;
	}

	/**
	 * 获取请求处理方式
	 * 
	 * @param request    HttpServletRequest
	 * @param name       请求参数头的名字
	 * @param handleEnum 系统配置的处理方式
	 * @return
	 */
	public static HandleEnum handleType(HttpServletRequest request, String name, HandleEnum handleEnum) {
		if (handleEnum != HandleEnum.AUTO) {
			return handleEnum;
		}
		// 获取请求中的处理方式
		handleEnum = handleType(request, name);
		return handleEnum == null ? HandleEnum.REDIRECT : handleEnum;

	}
}
