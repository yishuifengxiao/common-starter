package com.yishuifengxiao.common.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * http 工具类
 * 
 * @author yishui
 * @date 2020年6月23日
 * @version 1.0.0
 */
@Slf4j
public class HttpUtil {
	private static final RedirectStrategy STRATEGY = new DefaultRedirectStrategy();

	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 * 重定向到指定的url
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @param data
	 * @throws IOException
	 */
	public synchronized static void redirect(HttpServletRequest request, HttpServletResponse response, String url,
			Object data) throws IOException {
		request.getSession().setAttribute("info", data);
		STRATEGY.sendRedirect(request, response, url);
	}

	/**
	 * 向http响应流中推送数据并关闭响应流
	 * 
	 * @param resp
	 * @param result
	 */
	public synchronized static void out(HttpServletResponse response, Object data) {
		response.setStatus(HttpStatus.OK.value());
		// 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 允许使用的请求方法，以逗号隔开
		response.setHeader("Access-Control-Allow-Methods", "*");
		// 是否允许请求带有验证信息，
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(MAPPER.writeValueAsString(data));
			response.getWriter().flush();
			response.getWriter().close();
		} catch (Exception e) {
			log.info("返回响应数据时出现问题，出现问题的原因为 {}", e.getMessage());
		}
	}

	/**
	 * 打印请求头数据
	 * 
	 * @param request HttpServletRequest
	 * @return 用户希望返回的数据类型
	 */
	public static void stack(HttpServletRequest request) {
		log.debug("");
		log.debug("==start  用户请求的请求参数中包含的信息为 query start ===");
		Map<String, String[]> params = request.getParameterMap();
		if (null != params) {
			params.forEach((k, v) -> {
				log.debug("请求参数中的参数名字为 {},对应的值为 {}", k, StringUtils.join(v, " , "));
			});
		}
		log.debug("==end  用户请求的请求参数中包含的信息为  query end ===");
		log.debug("==start  用户请求的请求头中包含的信息为 header start ===");
		for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
			String name = e.nextElement();
			log.debug("请求头的名字为 {},对应的值为 {}", name, request.getHeader(name));
		}
		log.debug("==end  用户请求的请求头中包含的信息为 header end ===");
		log.debug("");
	}

}
