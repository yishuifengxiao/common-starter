package com.yishuifengxiao.common.code.sender;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * 验证码发送器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public interface CodeSender {
	/**
	 * 发送短信验证码<br/>
	 * <br/>
	 * 
	 * 
	 * 1. 对于短信验证码，一般来说标识符为发送目标的手机号<br/>
	 * 2. 对于邮件验证码，一般来说标识符为发送目标的邮箱地址<br/>
	 * 3. 对于图形验证码，一般来说为与用户约定的字符
	 * 
	 * @param <T>
	 * 
	 * @param request ServletWebRequest
	 * @param target  验证码的唯一标识符
	 * @param code    验证码内容
	 * @throws ValidateException
	 */
	<T extends ValidateCode> void send(ServletWebRequest request, String target, T code) throws ValidateException;
}