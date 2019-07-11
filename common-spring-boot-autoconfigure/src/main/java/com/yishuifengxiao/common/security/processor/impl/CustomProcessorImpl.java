package com.yishuifengxiao.common.security.processor.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.properties.SecurityProperties;
import com.yishuifengxiao.common.security.eunm.HandleEnum;
import com.yishuifengxiao.common.security.processor.CustomProcessor;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.utils.HeaderUtil;

/**
 * handler协助处理器的默认实现
 * 
 * @author yishui
 * @Date 2019年4月2日
 * @version 1.0.0
 */
public class CustomProcessorImpl implements CustomProcessor {
	private final static Logger log = LoggerFactory.getLogger(CustomProcessorImpl.class);
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private ObjectMapper objectMapper;

	private SecurityProperties securityProperties;

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, HandleEnum type, String url,
			Response result) throws IOException, ServletException {

		switch (type) {
		case REDIRECT:
			redirect(request, response, url, result);
			break;
		case AUTO:
			autoHandle(request, response, result, url);
			break;
		case DEFAULT:// 另一种情况已经排除
			send(request, response, result);
			break;
		case JSON:
			send(request, response, result);
			break;
		default:
			send(request, response, result);
			break;
		}

	}

	/**
	 * 内容协商处理
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 * @param type
	 *            用户希望得到的数据类型
	 * @throws IOException
	 * @throws JsonProcessingException
	 * @throws ServletException
	 */
	@SuppressWarnings("rawtypes")
	private void autoHandle(HttpServletRequest request, HttpServletResponse response, Response result, String url)
			throws JsonProcessingException, IOException, ServletException {

		String headerName = securityProperties.getHandler().getHeaderName();

		// 用户希望返回的数据的类型
		HandleEnum type = HeaderUtil.getType(request, headerName);

		if (type == HandleEnum.JSON) {
			send(request, response, result);
		} else {
			// 默认处理
			redirect(request, response, url, result);
		}

	}

	/**
	 * 向前端发送json信息
	 * 
	 * @param request
	 * @param response
	 * @param result
	 * @param objectMapper
	 * @throws JsonProcessingException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private void send(HttpServletRequest request, HttpServletResponse response, Response result)
			throws JsonProcessingException, IOException {
		log.debug("================================> 最终处理方式为 JSON ,发送的数据为{}", result);
		response.setStatus(HttpStatus.OK.value());
		response.setHeader("Access-Control-Allow-Origin", "*");// 允许跨域访问的域，可以是一个域的列表，也可以是通配符"*"
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");// 允许使用的请求方法，以逗号隔开
		response.setHeader("Access-Control-Allow-Credentials", "true");// 是否允许请求带有验证信息，
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(result));

	}

	/**
	 * 重定向到指定的url
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private void redirect(HttpServletRequest request, HttpServletResponse response, String url, Response result)
			throws IOException {
		log.debug("================================> 最终处理方式为 Redirect ,目标url为{}", url);
		request.getSession().setAttribute("info", result);
		redirectStrategy.sendRedirect(request, response, url);
	}

	public CustomProcessorImpl(ObjectMapper objectMapper, SecurityProperties securityProperties) {

		this.objectMapper = objectMapper;
		this.securityProperties = securityProperties;
	}

	public CustomProcessorImpl() {

	}

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

}
