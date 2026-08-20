/**
 * 
 */
package com.yishuifengxiao.common.code.entity;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

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
	 * 图形验证码写出格式，与 {@code ImageCodeSender} 保持一致
	 */
	private static final String IMAGE_FORMAT = "JPEG";

	/**
	 * 验证码图片
	 */
	@JsonIgnore
	private transient BufferedImage image;

	/**
	 * 验证码图片的 Base64 编码（由 {@link #image} 写出为 JPEG 后编码得到）
	 */
	private String base64Image;

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
		setImage(image);
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
	 * 设置验证码图片，同时刷新 {@link #base64Image}
	 * 
	 * @param image 验证码图片
	 */
	@JsonIgnore
	public void setImage(BufferedImage image) {
		this.image = image;
		this.base64Image = encodeToBase64(image);
	}

	/**
	 * 获取验证码图片的 Base64 编码
	 * 
	 * @return 图片的 Base64 字符串，图片为空或编码失败时为 {@code null}
	 */
	public String getBase64Image() {
		return base64Image;
	}

	/**
	 * 设置验证码图片的 Base64 编码
	 * 
	 * @param base64Image 图片的 Base64 字符串
	 */
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	/**
	 * 将 {@link BufferedImage} 以 JPEG 写出后转为 Base64
	 * 
	 * @param image 待编码的图片
	 * @return Base64 字符串；图片为空或写出失败时返回 {@code null}
	 */
	private static String encodeToBase64(BufferedImage image) {
		if (image == null) {
			return null;
		}
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			if (!ImageIO.write(image, IMAGE_FORMAT, out)) {
				return null;
			}
			return Base64.getEncoder().encodeToString(out.toByteArray());
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
