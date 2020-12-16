/**
 * 
 */
package com.yishuifengxiao.common.code.processor;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.eunm.CodeType;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 验证码处理器
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public interface CodeProcessor {
	/**
	 * 创建校验码并发送验证码
	 * 
	 * @param request  ServletWebRequest
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws ValidateException
	 */
	ValidateCode create(ServletWebRequest request, CodeType codeType) throws ValidateException;

	/**
	 * 校验验证码<br/>
	 * <br/>
	 * 自动从请求获取输入的验证码
	 * 
	 * @param request  ServletWebRequest
	 * @param codeType 验证码的类型
	 * @throws ValidateException
	 */
	void validate(ServletWebRequest request, CodeType codeType) throws ValidateException;

	/**
	 * 校验验证码<br/>
	 * <br/>
	 * 根据用户输入的验证码进行校验<br/>
	 * 生成验证码存储时的key<br/>
	 * 对于短信验证码,key为手机号<br/>
	 * 对于邮件验证码，key为邮箱<br/>
	 * 对于图像验证码，key为sessionid或指定的值
	 * 
	 * @param request
	 * @param key
	 * @param codeInRequest
	 * @throws ValidateException
	 */
	void validate(ServletWebRequest request, String key, String codeInRequest) throws ValidateException;
}
