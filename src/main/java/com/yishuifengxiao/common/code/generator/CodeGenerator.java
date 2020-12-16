/**
 * 
 */
package com.yishuifengxiao.common.code.generator;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.extractor.CodeExtractor;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 验证码生成器<br/>
 * <br/>
 * 
 * 作用如下：<br/>
 * 1. 生成验证码<br/>
 * 2. 生成验证码存储时的key<br/>
 * 3. 获取请求中携带的验证码<br/>
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public interface CodeGenerator {
	/**
	 * 生成验证码
	 * 
	 * @param servletWebRequest
	 * @return
	 */
	ValidateCode generate(ServletWebRequest servletWebRequest);

	/**
	 * 生成验证码存储时的key<br/>
	 * 对于短信验证码,key为手机号<br/>
	 * 对于邮件验证码，key为邮箱<br/>
	 * 对于图像验证码，key为sessionid或指定的值
	 * 
	 * @param request
	 * @param codeExtractor
	 * @return
	 * @throws ValidateException
	 */
	String generateKey(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException;

	/**
	 * 获取请求中携带的验证码
	 * 
	 * @param request
	 * @param codeExtractor
	 * @return
	 * @throws ValidateException
	 */
	String getCodeInRequest(ServletWebRequest request, CodeExtractor codeExtractor) throws ValidateException;
}
