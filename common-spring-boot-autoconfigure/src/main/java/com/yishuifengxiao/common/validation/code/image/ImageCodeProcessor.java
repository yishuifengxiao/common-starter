package com.yishuifengxiao.common.validation.code.image;

import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.properties.CodeProperties;
import com.yishuifengxiao.common.tool.exception.ValidateException;
import com.yishuifengxiao.common.validation.entity.ImageCode;
import com.yishuifengxiao.common.validation.generator.CodeGenerator;
import com.yishuifengxiao.common.validation.processor.AbstractCodeProcessor;
import com.yishuifengxiao.common.validation.repository.CodeRepository;

/**
 * 图形验证码处理器
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class ImageCodeProcessor extends AbstractCodeProcessor<ImageCode> {
	private final static Logger log = LoggerFactory.getLogger(ImageCodeProcessor.class);
	private final static String COOKIE_NAME = "SESSION";

	@Override
	protected void send(ServletWebRequest request, ImageCode imageCode) throws ValidateException {
		// 将图片输出到页面
		try {
			ImageIO.write(imageCode.getImage(), "JPEG", request.getResponse().getOutputStream());
		} catch (IOException e) {
			log.info("输出图形验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("输出图形验证码失败");
		}
		log.debug("输出图片验证码到页面,验证码为 {}", imageCode);

	}

	@Override
	protected String generateKey(ServletWebRequest request) throws ValidateException {
		// 获取到验证码对应的key值
		String key = request.getParameter(this.codeProperties.getImage().getCodeKey());

		if (StringUtils.isBlank(key)) {
			Cookie[] cookies = request.getRequest().getCookies();
			if (cookies != null && StringUtils.isBlank(key)) {
				for (Cookie cookie : cookies) {
					if (StringUtils.equalsIgnoreCase(COOKIE_NAME, cookie.getName())) {
						key = cookie.getValue();
						break;
					}
				}
			}
			key = StringUtils.isNotBlank(key) ? key : request.getSessionId();
		}

		log.debug("图形验证码生成中生成的key 为{}", key);
		return key;
	}

	@Override
	protected String getCodeInRequest(ServletWebRequest request) throws ValidateException {

		String codeInRequest = null;
		try {
			codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),
					this.codeProperties.getImage().getCodeValue());
		} catch (ServletRequestBindingException e) {
			log.info("从请求中获取验证码失败，失败的原因为 {}", e.getMessage());
			throw new ValidateException("从请求中获取验证码失败");
		}
		return codeInRequest;

	}

	public ImageCodeProcessor() {

	}

	public ImageCodeProcessor(Map<String, CodeGenerator> codeGenerators, CodeRepository repository,
			CodeProperties codeProperties) {
		super(codeGenerators, repository, codeProperties);
	}

}
