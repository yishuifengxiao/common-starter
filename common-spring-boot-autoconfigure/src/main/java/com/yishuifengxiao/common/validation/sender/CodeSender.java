package com.yishuifengxiao.common.validation.sender;

import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.eunm.CodeType;

/**
 * 短信验证码发送接口
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public interface CodeSender<T> {
	/**
	 * 发送短信验证码
	 * 
	 * @param target
	 *            发送目标
	 * @param smsCode
	 *            验证码内容
	 * @param codeType
	 *            验证码的类型
	 * @throws ValidateException
	 */
	void send(String target, T smsCode, CodeType codeType) throws ValidateException;
}