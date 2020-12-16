package com.yishuifengxiao.common.aop;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求的上下文
 * 
 * @author yishui
 * @date 2020年6月17日
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7967527202884314519L;
	/**
	 * 请求的缓存的名字
	 */
	public static final String CACHE_KEY = "REQUEST_CONTEXT_CACHE_KEY";

	/**
	 * 请求的序列号
	 */
	private String requestId;
	/**
	 * 请求的类的名字
	 */
	private String className;
	/**
	 * 请求的方法的名字
	 */
	private String methodName;
	/**
	 * 请求的参数
	 */
	private Object[] args;
	/**
	 * 被请求的目标的完整的名字
	 */
	private String fullName;
	/**
	 * 请求时间
	 */
	private LocalDateTime time;

}
