package com.yishuifengxiao.common.code.sender.impl;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.tool.exception.ValidateException;

import lombok.extern.slf4j.Slf4j;

/**
 * 短信验证码发送器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
@Slf4j
public class SmsCodeSender implements CodeSender {

	@Override
	public <T extends ValidateCode> void send(ServletWebRequest request, String target, T code)
			throws ValidateException {
		log.info("【短信验证码发送器】向手机号 {} 发送短信验证码，验证码的内容为 {} ", target, code);
	}

}
