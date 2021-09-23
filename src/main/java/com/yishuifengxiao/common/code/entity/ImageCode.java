/**
 * 
 */
package com.yishuifengxiao.common.code.entity;

import java.awt.image.BufferedImage;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 图形验证码
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageCode extends ValidateCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7985276921320113850L;

	/**
	 * 验证码图片
	 */
	@JsonIgnore
	private transient BufferedImage image;

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 过期时间，单位秒
	 * @param code                验证码内容
	 */
	public ImageCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}

	/**
	 * 构造函数
	 */
	public ImageCode() {

	}

	/**
	 * 构造函数
	 * 
	 * @param expireTimeInSeconds 验证码的失效日期
	 * @param code                验证码的内容
	 * @param image               验证码的图片
	 */
	public ImageCode(long expireTimeInSeconds, String code, BufferedImage image) {
		super(expireTimeInSeconds, code);
		this.image = image;
	}

	/**
	 * 获取验证码图片
	 * 
	 * @return 验证码图片
	 */
	@JsonIgnore
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * 设置验证码图片
	 * 
	 * @param image 验证码图片
	 */
	@JsonIgnore
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
