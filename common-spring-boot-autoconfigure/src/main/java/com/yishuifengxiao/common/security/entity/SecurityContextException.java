package com.yishuifengxiao.common.security.entity;

import java.io.Serializable;
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
	 * 引起异常的原因
	 */
	private Exception exception;
	/**
	 * 引起异常的资源URI
	 */
	private String uri;
	
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
	 * 获取异常信息对应的URI
	 * 
	 * @return
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 异常信息对应的URI
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	public SecurityContextException(Exception exception, String uri) {
		this.exception = exception;
		this.uri = uri;
	}

	public SecurityContextException() {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SecurityException [exception=");
		builder.append(exception);
		builder.append(", uri=");
		builder.append(uri);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exception == null) ? 0 : exception.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SecurityContextException other = (SecurityContextException) obj;
		if (exception == null) {
			if (other.exception != null)
				return false;
		} else if (!exception.equals(other.exception))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}



}
