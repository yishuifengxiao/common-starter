package com.yishuifengxiao.common.security.context;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.yishuifengxiao.common.security.entity.SecurityContextException;



/**
 * 自定义异常信息上下文
 * 
 * @author yishui
 * @date 2019年10月18日
 * @version 1.0.0
 */
public interface SecurityContext extends Serializable {

	/**
	 * 获取存储的异常
	 * 
	 * @return
	 */
	SecurityContextException getSecurityExcepion();

	/**
	 * 存储异常信息
	 * 
	 * @param excption 异常信息
	 */
	void setSecurityExcepion(SecurityContextException excption);

	/**
	 * 存储异常信息
	 * 
	 * @param excption 引起异常的原因
	 * @param request      引起异常的请求
	 */
	void setSecurityExcepion(HttpServletRequest request,Exception excption);

	/**
	 * 存储异常信息
	 * 
	 * @param excption 引起异常的原因
	 */
	void setSecurityExcepion(Exception excption);

	/**
	 * 存储异常信息
	 * 
	 * @param request 引起异常的uri
	 */
	void setSecurityExcepion(HttpServletRequest request);

}
