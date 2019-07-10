package com.yishuifengxiao.common.properties.code;

import com.yishuifengxiao.common.constant.CodeConstant;

/**
 * 图形验证码的参数配置
 * 
 * @author yishui
 * @date 2019年1月23日
 * @version 0.0.1
 */
public class ImageCodeProperties extends SmsCodeProperties {
	/**
	 * 验证码的宽度
	 */
	private Integer width = CodeConstant.DEFAULT_IMAGE_CODE_WIDTH;
	/**
	 * 验证码的高度
	 */
	private Integer height = CodeConstant.DEFAULT_IMAGE_CODE_HEIGHT;

	public ImageCodeProperties() {
		// 设置验证码的标识符为 image
		this.setCodeKey(CodeConstant.CODE_IMAGE_KEY);
		// 设置验证码对应的值的参数为 image_code
		this.setCodeValue(CodeConstant.CODE_IMAGE_VALUE);
	}

	/**
	 * 验证码的宽度,默认为70
	 * 
	 * @return
	 */
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * 验证码的高度,默认为 28
	 * 
	 * @return
	 */
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

}