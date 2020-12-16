/**
 * 
 */
package com.yishuifengxiao.common.code.entity;

import java.awt.image.BufferedImage;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 图形验证码
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
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
	
	public ImageCode(long expireTimeInSeconds, String code) {
		super(expireTimeInSeconds, code);
	}
	
	
    public ImageCode(long expireTimeInSeconds) {
		super(expireTimeInSeconds);
	}


	public ImageCode() {

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

	@JsonIgnore
	public BufferedImage getImage() {
		return image;
	}
	
	@JsonIgnore
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return super.toString();
	}



}
