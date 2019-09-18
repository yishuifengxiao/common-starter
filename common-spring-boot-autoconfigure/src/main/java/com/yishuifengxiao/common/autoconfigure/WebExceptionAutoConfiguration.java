package com.yishuifengxiao.common.autoconfigure;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
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

import com.yishuifengxiao.common.tool.entity.Response;
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
public class WebExceptionAutoConfiguration {
	private static Logger logger = LoggerFactory.getLogger(WebExceptionAutoConfiguration.class);

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
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Response<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法");
		logger.warn("请求{} 不支持当前请求方法,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;

	}

	/**
	 * 415 - Unsupported Media Type
	 */
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
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
	 * 当尝试插入或更新数据导致违反主键或唯一约束时引发异常。请注意，这不一定是纯关系概念；大多数数据库类型都需要唯一主键。
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Response<String> handle(DuplicateKeyException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "已经存在相似的数据,不能重复添加");
		logger.warn("请求{} 插入数据到数据库时出现问题,失败的原因为 {}  ", response.getId(), e.getMessage());
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
		Response<String> response = ExceptionUtil.extract(e);
		logger.warn("【全局异常拦截】请求{} 请求失败,拦截到未知异常{}", response.getId(), e);
		logger.warn("请求{} 请求失败,失败的原因为 {}  ", response.getId(), e.getMessage());
		return response;
	}

	@PostConstruct
	public void checkConfig() {

		logger.debug("【全局异常拦截】 开启全局异常拦截自定义配置为 {}");
	}

}