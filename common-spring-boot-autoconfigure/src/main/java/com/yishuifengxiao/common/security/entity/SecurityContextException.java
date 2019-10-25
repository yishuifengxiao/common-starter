package com.yishuifengxiao.common.security.entity;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
/**
 * 自定义异常信息存储
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public class SecurityContextException  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7021898026807517304L;
	
	/**
	 * 引起异常的请求
	 */
	private HttpServletRequest request;
	/**
	 * 引起异常的原因
	 */
	private Exception exception;

	
	public void set() {}

	/**
	 * 获取异常信息
	 * 
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * 设置异常信息
	 * 
	 * @param exception
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}


    /**
     * 获取引起异常的请求
     * @return
     */
	public HttpServletRequest getRequest() {
		return request;
	}
    /**
     * 设置引起异常的请求
     * @param request
     */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	

	public SecurityContextException(HttpServletRequest request, Exception exception) {
		this.request = request;
		this.exception = exception;
	}

	public SecurityContextException() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SecurityContextException [exception=");
		builder.append(exception);
		builder.append(", request=");
		builder.append(request);
		builder.append("]");
		return builder.toString();
	}




}
