/**
 * 
 */
package com.yishuifengxiao.common.validation.processor;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 验证码处理器
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public interface CodeProcessor {
	/**
	 * 创建校验码
	 * 
	 * @param request
	 * @throws Exception
	 */
	void create(ServletWebRequest request) throws ValidateException;

	/**
	 * 校验验证码
	 * 
	 * @param servletWebRequest
	 * @throws Exception
	 */
	void validate(ServletWebRequest servletWebRequest) throws ValidateException;
}
