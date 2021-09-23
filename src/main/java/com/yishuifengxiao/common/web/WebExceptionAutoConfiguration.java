package com.yishuifengxiao.common.web;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.yishuifengxiao.common.support.ErrorUtil;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.random.UID;
import com.yishuifengxiao.common.tool.utils.RegexUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常捕获自动配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice
@ResponseBody
@ConditionalOnProperty(prefix = "yishuifengxiao.error", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class WebExceptionAutoConfiguration {
	@Autowired
	private WebFilterProperties webProperties;

	/**
	 * 400 - Bad Request
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException e) {

		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数解析失败").setId(ssid);
		log.warn("【易水组件】请求{} 参数解析失败,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 400 - Bad Request
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalArgumentException.class)
	public Object handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数不符合要求").setId(ssid);
		log.warn("【易水组件】 请求{} 参数解析失败,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 405 - Method Not Allowed
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object handleHttpRequestMethodNotSupportedException(HttpServletRequest request,
			HttpRequestMethodNotSupportedException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法")
				.setId(ssid);
		log.warn("【易水组件】 请求{}  不支持当前请求方法,失败的原因为 {}  ", ssid, e.getMessage());
		return response;

	}

	/**
	 * 415 - Unsupported Media Type
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Object handleHttpMediaTypeNotSupportedException(HttpServletRequest request,
			HttpMediaTypeNotSupportedException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型")
				.setId(ssid);
		log.warn("【易水组件】 请求{} 不支持当前媒体类型,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(NullPointerException.class)
	public Object handleNullPointerException(HttpServletRequest request, NullPointerException e) {
		String ssid = this.getRequestId(request);
		e.printStackTrace();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败").setId(ssid);
		log.warn("【易水组件】 请求{} 请求失败,失败的原因为空指针异常  ", ssid);
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ServletException.class)
	public Object handleServletException(HttpServletRequest request, ServletException e) {
		String ssid = this.getRequestId(request);
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败").setId(ssid);
		log.warn("【易水组件】   请求{} 请求失败,失败的原因为{}  ", msg, ssid);
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IOException.class)
	public Object handleIoException(HttpServletRequest request, IOException e) {
		String ssid = this.getRequestId(request);
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败").setId(ssid);
		log.warn("【易水组件】  请求{}  请求失败,失败的原因为{}  ", ssid, msg);
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingServletRequestParameterException(HttpServletRequest request,
			MissingServletRequestParameterException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误").setId(ssid);
		log.warn("【易水组件】  请求{}  请求参数有误,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Object handleMethodArgumentTypeMismatchException(HttpServletRequest request,
			MethodArgumentTypeMismatchException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误").setId(ssid);
		log.warn("【易水组件】   请求{} 请求参数有误,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 参数验证异常
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object handle(HttpServletRequest request, ValidationException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数").setId(ssid);
		log.warn("【易水组件】   请求{} 请求参数有误,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 参数验证异常
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object handle(HttpServletRequest request, ConstraintViolationException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数").setId(ssid);
		log.warn("【易水组件】请求{} 请求参数有误,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 数组越界 - Internal Server Error
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public Object handleIndexOutOfBoundsException(HttpServletRequest request, IndexOutOfBoundsException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未查询到对应的数据")
				.setId(ssid);
		log.warn("【易水组件】请求{} 请求失败,出现数组越界,失败的原因为 {}  ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 500 - 自定义异常
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(CustomException.class)
	public Object handleCustomException(HttpServletRequest request, CustomException e) {
		String ssid = this.getRequestId(request);
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
				.setId(ssid);
		log.warn("【易水组件】( 自定义异常)  请求{} 请求失败,失败的原因为 {} ", ssid, e.getMessage());
		return response;
	}

	/**
	 * 500 - IllegalStateException
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalStateException.class)
	public Object handleIllegalStateException(HttpServletRequest request, IllegalStateException e) {
		String ssid = this.getRequestId(request);
		Response<Object> response = Response.error(ErrorUtil.getErrorMsg(e, "请求失败")).setId(ssid);
		log.warn("【易水组件】 请求{} 请求失败,拦截到未知异常{}", ssid, e.getMessage());
		return response;
	}

	/**
	 * 500 - IllegalStateException
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(RuntimeException.class)
	public Object handleRuntimeException(HttpServletRequest request, RuntimeException e) {
		String ssid = this.getRequestId(request);
		Response<Object> response = Response.error(ErrorUtil.getErrorMsg(e, "请求失败")).setId(ssid);
		log.warn("【易水组件】 ( 运行时异常)请求{} 请求失败,拦截到运行时异常{}", ssid, e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 * 
	 * @param request HttpServletRequest
	 * @param e       希望捕获的异常
	 * @return 发生异常捕获之后的响应
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletRequest request, Exception e) {
		String ssid = this.getRequestId(request);
		Response<Object> response = Response.error(ErrorUtil.getErrorMsg(e, "请求失败")).setId(ssid);
		log.warn("【易水组件】 ( 全局异常拦截)请求{}   请求失败,拦截到未知异常{}", ssid, e.getMessage());
		return response;
	}

	/**
	 * 获取请求的id
	 * 
	 * @param request HttpServletRequest
	 * @return 请求的ID
	 */
	private String getRequestId(HttpServletRequest request) {
		String ssid = (String) request.getAttribute(webProperties.getSsidName());
		return StringUtils.isBlank(ssid) ? UID.uuid() : ssid;
	}

	@PostConstruct
	public void checkConfig() {

		log.trace("【易水组件】: 开启 <全局异常拦截> 相关的配置");
	}

}