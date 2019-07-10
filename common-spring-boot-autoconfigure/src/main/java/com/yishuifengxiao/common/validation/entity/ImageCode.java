/**
 * 
 */
package com.yishuifengxiao.common.validation.entity;

import java.awt.image.BufferedImage;

/**
 * 图形验证码
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public class ImageCode extends SmsCode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7985276921320113850L;

	/**
	 * 验证码图片
	 */
	private transient BufferedImage image;
	
	private ImageCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}
    /**
     * 
     * @param expireTimeInSeconds 验证码的失效日期
     * @param code 验证码的内容
     * @param image 验证码的图片
     */
	public ImageCode(long expireTimeInSeconds, String code, BufferedImage image) {
		super(expireTimeInSeconds, code);
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return super.toString();
	}



}
