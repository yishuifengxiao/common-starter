package com.yishuifengxiao.common.code.sender.impl;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.constant.ErrorCode;
import com.yishuifengxiao.common.code.entity.ImageCode;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.sender.CodeSender;
import com.yishuifengxiao.common.tool.exception.ValidateException;

import lombok.extern.slf4j.Slf4j;

/**
 * 图形验证码发送器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
@Slf4j
public class ImageCodeSender implements CodeSender {

	@Override
	public <T extends ValidateCode> void send(ServletWebRequest request, String target, T code)
			throws ValidateException {
		// 将图片输出到页面
		try {
			ImageIO.write(((ImageCode) code).getImage(), "JPEG", request.getResponse().getOutputStream());
		} catch (IOException e) {
			log.info("输出图形验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException(ErrorCode.CODE_SEND_ERROR, "输出图形验证码失败");
		}

	}

}
