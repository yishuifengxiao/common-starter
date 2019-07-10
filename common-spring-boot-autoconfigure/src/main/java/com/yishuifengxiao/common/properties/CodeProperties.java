/**
 * 
 */
package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.properties.code.EmailCodeProperties;
import com.yishuifengxiao.common.properties.code.ImageCodeProperties;
import com.yishuifengxiao.common.properties.code.SmsCodeProperties;

/**
 * 验证码相关的属性配置
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
@ConfigurationProperties(prefix = "yishuifengxiao.code")
public class CodeProperties {
	/**
	 * 图形验证码相关的配置
	 */
	private ImageCodeProperties image = new ImageCodeProperties();
	/**
	 * 短信验证码相关的配置
	 */
	private SmsCodeProperties sms = new SmsCodeProperties();
	/**
	 * 邮箱验证码的相关配置
	 */
	private EmailCodeProperties email = new EmailCodeProperties();

	public ImageCodeProperties getImage() {
		return image;
	}

	public void setImage(ImageCodeProperties image) {
		this.image = image;
	}

	public SmsCodeProperties getSms() {
		return sms;
	}

	public void setSms(SmsCodeProperties sms) {
		this.sms = sms;
	}

	public EmailCodeProperties getEmail() {
		return email;
	}

	public void setEmail(EmailCodeProperties email) {
		this.email = email;
	}

}
