/**
 * 
 */
package com.yishuifengxiao.common.validation.generator;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.validation.entity.ValidateCode;

/**
 * 验证码生成器
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public interface CodeGenerator {
	/**
	 * 生成验证码
	 * @param servletWebRequest
	 * @return
	 */
	ValidateCode generate(ServletWebRequest servletWebRequest);
}
