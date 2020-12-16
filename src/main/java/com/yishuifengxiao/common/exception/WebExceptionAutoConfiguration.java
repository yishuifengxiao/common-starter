package com.yishuifengxiao.common.exception;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

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

import com.yishuifengxiao.common.aop.RequestContext;
import com.yishuifengxiao.common.support.ErrorMsgUtil;
import com.yishuifengxiao.common.tool.context.SessionStorage;
import com.yishuifengxiao.common.tool.entity.Response;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.random.UID;
import com.yishuifengxiao.common.tool.utils.RegexUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 * 
 * @author yishui
 * @date 2018年7月9日
 * @version 0.0.1
 */
@Slf4j
@ControllerAdvice
@ResponseBody
@ConditionalOnProperty(prefix = "yishuifengxiao.error", name = {
		"enable" }, havingValue = "true", matchIfMissing = true)
public class WebExceptionAutoConfiguration {

	/**
	 * 异常信息提取工具类
	 */
	@Autowired
	private ErrorMsgUtil errorMsgUtil;

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数解析失败")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 参数解析失败,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 400 - Bad Request
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalArgumentException.class)
	public Object handleIllegalArgumentException(IllegalArgumentException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "参数不符合要求")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 参数解析失败,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 405 - Method Not Allowed
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前请求方法")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 不支持当前请求方法,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;

	}

	/**
	 * 415 - Unsupported Media Type
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Object handleHttpMediaTypeNotSupportedException(Exception e) {
		Response<String> response = new Response<String>(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持当前媒体类型")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 不支持当前媒体类型,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(NullPointerException.class)
	public Object handleNullPointerException(NullPointerException e) {
		e.printStackTrace();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求失败,失败的原因为空指针异常  ", SessionStorage.get(RequestContext.CACHE_KEY));
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(ServletException.class)
	public Object handleServletException(ServletException e) {
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败").setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求失败,失败的原因为{}  ", SessionStorage.get(RequestContext.CACHE_KEY), msg);
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IOException.class)
	public Object handleIoException(IOException e) {
		String msg = e.getMessage();
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				RegexUtil.containChinese(msg) ? msg : "请求失败").setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求失败,失败的原因为{}  ", SessionStorage.get(RequestContext.CACHE_KEY), msg);
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求参数有误,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "请求参数有误")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求参数有误,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
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
	public Object handle(ValidationException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求参数有误,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
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
	public Object handle(ConstraintViolationException e) {
		Response<String> response = new Response<String>(HttpStatus.BAD_REQUEST.value(), "非法参数")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求参数有误,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 数组越界 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public Object handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未查询到对应的数据")
				.setId(this.getRequestId());
		log.warn("【易水组件】请求{} 请求失败,出现数组越界,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 500 - 自定义异常
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(CustomException.class)
	public Object handleCustomException(CustomException e) {
		Response<String> response = new Response<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage())
				.setId(this.getRequestId());
		log.warn("【易水组件】( 自定义异常) 请求{} 请求失败,失败的原因为 {} ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 500 - IllegalStateException
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(IllegalStateException.class)
	public Object handleIllegalStateException(IllegalStateException e) {
		Response<Object> response = Response.error(errorMsgUtil.getErrorMsg(e, "请求失败")).setId(this.getRequestId());
		log.warn("【易水组件】 请求{} 请求失败,拦截到未知异常{}", response.getId(), e);
		log.warn("请求{} 请求失败,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 500 - IllegalStateException
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(RuntimeException.class)
	public Object handleRuntimeException(RuntimeException e) {
		Response<Object> response = Response.error(errorMsgUtil.getErrorMsg(e, "请求失败")).setId(this.getRequestId());
		log.warn("【易水组件】 ( 运行时异常) 请求{}请求{} 请求失败,拦截到运行时异常{}", response.getId(), e);
		log.warn("请求{} 请求失败,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 500 - Internal Server Error
	 */
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(Exception.class)
	public Object handleException(Exception e) {
		Response<Object> response = Response.error(errorMsgUtil.getErrorMsg(e, "请求失败")).setId(this.getRequestId());
		log.warn("【易水组件】 ( 全局异常拦截) 请求{} 请求失败,拦截到未知异常{}", response.getId(), e);
		log.warn("请求{} 请求失败,失败的原因为 {}  ", SessionStorage.get(RequestContext.CACHE_KEY), e.getMessage());
		return response;
	}

	/**
	 * 获取请求的id
	 * 
	 * @return 请求的ID
	 */
	private String getRequestId() {
		RequestContext requestContext = (RequestContext) SessionStorage.get(RequestContext.CACHE_KEY);
		return null == requestContext ? UID.uuid() : requestContext.getRequestId();
	}

	@PostConstruct
	public void checkConfig() {

		log.debug("【易水组件】: 开启 <全局异常拦截功能> 相关的配置");
	}

}