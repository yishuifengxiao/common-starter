package com.yishuifengxiao.common.autoconfigure;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import com.yishuifengxiao.common.properties.ExceptionProperties;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.utils.RegexUtil;
import com.yishuifengxiao.common.utils.ExceptionUtil;

/**
 * 全局异常处理类
 * 
 * @author yishui
 * @date 2018年7月9日
 * @version 0.0.1
 */
@ControllerAdvice
@ResponseBody
@EnableConfigurationProperties(ExceptionProperties.class)
public class WebExceptionAutoConfiguration {
	private static Logger logger = LoggerFactory.getLogger(WebExceptionAutoConfiguration.class);
	
	@Autowired
	private ExceptionProperties exceptionProperties;

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Response<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数解析失败");
		logger.warn("请求{} 参数解析失败,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalArgumentException.class)
	public Response<String> handleIllegalArgumentException(IllegalArgumentException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数不符合要求");
		logger.warn("请求{} 参数解析失败,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 405 - Method Not Allowed
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Response<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法");
		logger.warn("请求{} 不支持当前请求方法,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;

	}

	/**
	 * 415 - Unsupported Media Type
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Response<String> handleHttpMediaTypeNotSupportedException(Exception e) {
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型");
		logger.warn("请求{} 不支持当前媒体类型,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(NullPointerException.class)
	public Response<String> handleNullPointerException(NullPointerException e) {
		e.printStackTrace();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败");
		logger.warn("请求{} 请求失败,失败的原因为空指针异常  ", response.getId());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ServletException.class)
	public Response<String> handleServletException(ServletException e) {
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败");
		logger.warn("请求{} 请求失败,失败的原因为{}  ", response.getId(), msg);
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IOException.class)
	public Response<String> handleIoException(IOException e) {
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败");
		logger.warn("请求{} 请求失败,失败的原因为{}  ", response.getId(), msg);
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Response<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
		logger.warn("请求{} 请求参数有误,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Response<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误");
		logger.warn("请求{} 请求参数有误,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 参数验证异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Response<String> handle(ValidationException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数");
		logger.warn("请求{} 请求参数有误,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 参数验证异常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Response<String> handle(ConstraintViolationException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数");
		logger.warn("请求{} 请求参数有误,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}



	/**
	 * 数组越界 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public Response<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未查询到对应的数据");
		logger.warn("请求{} 请求失败,出现数组越界,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Response<String> handleException(Exception e) {
		Response<String> response = ExceptionUtil.extract(exceptionProperties.getMap(),e);
		logger.warn("【全局异常拦截】请求{} 请求失败,拦截到未知异常{}", response.getId(), e);
		logger.warn("请求{} 请求失败,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	@PostConstruct
	public void checkConfig() {

		logger.debug("【全局异常拦截】 开启全局异常拦截自定义配置为 {}");
	}

}