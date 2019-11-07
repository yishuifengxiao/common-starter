/**
 * 
 */
package com.yishuifengxiao.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.constant.CodeConstant;

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

	/**
	 * 邮件验证码相关的配置
	 * 
	 * @author yishui
	 * @date 2019年10月18日
	 * @version 1.0.0
	 */
	public static class EmailCodeProperties extends SmsCodeProperties {

		/**
		 * 验证码邮箱的内容模板
		 */
		private String contentTemplate = "您的验证码的内容为{0} ,验证码的有效时间为 {1} 秒";

		/**
		 * 验证码邮箱的标题
		 */
		private String title = "帐号保护验证";

		public EmailCodeProperties() {
			this.setExpireIn(60 * 30);
			// 设置验证码的标识符为 email
			this.setCodeKey(CodeConstant.CODE_EMAIL_KEY);
			// 设置验证码对应的值的参数为 email_code
			this.setCodeValue(CodeConstant.CODE_EMAIL_VALUE);
		}

		/**
		 * 获取验证码邮箱的内容模板
		 * 
		 * @return
		 */
		public String getContentTemplate() {
			return contentTemplate;
		}

		/**
		 * 设置验证码邮箱的内容模板
		 * 
		 * @param contentTemplate
		 */
		public void setContentTemplate(String contentTemplate) {
			this.contentTemplate = contentTemplate;
		}

		/**
		 * 获取验证码邮箱的标题
		 * 
		 * @return
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * 设置验证码邮箱的标题
		 * 
		 * @param title
		 */
		public void setTitle(String title) {
			this.title = title;
		}

	}

	/**
	 * 图形验证码的参数配置
	 * 
	 * @author yishui
	 * @date 2019年1月23日
	 * @version 0.0.1
	 */
	public static class ImageCodeProperties extends SmsCodeProperties {
		/**
		 * 验证码的宽度
		 */
		private Integer width = CodeConstant.DEFAULT_IMAGE_CODE_WIDTH;
		/**
		 * 验证码的高度
		 */
		private Integer height = CodeConstant.DEFAULT_IMAGE_CODE_HEIGHT;

		/**
		 * 是否生成干扰条纹背景，默认为false
		 */
		private Boolean fringe = CodeConstant.IS_FRINGE;

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

		/**
		 * 获取是否生成干扰条纹背景，默认为false
		 * 
		 * @return
		 */
		public Boolean getFringe() {
			return fringe;
		}

		/**
		 * 设置是否生成干扰条纹背景，默认为false
		 * 
		 * @param fringe
		 */
		public void setFringe(Boolean fringe) {
			this.fringe = fringe;
		}

	}

	/**
	 * 短信验证码的配置参数
	 * 
	 * @author yishui
	 * @date 2019年1月23日
	 * @version 0.0.1
	 */
	public static class SmsCodeProperties {
		/**
		 * 验证码的长度,默认为4
		 */
		private Integer length = CodeConstant.DEFAULT_CODE_LENGTH;
		/**
		 * 验证码的失效时间，单位秒，默认为300s
		 */
		private Integer expireIn = CodeConstant.DEFAULT_EXPIREIN;
		/**
		 * 验证码是否包含字母,默认包含
		 */
		private Boolean isContainLetter = CodeConstant.IS_CONTAIN_LETTERS;
		/**
		 * 验证码是否包含数字,默认包含
		 */
		private Boolean isContainNumber = CodeConstant.IS_CONTAIN_NUMBERS;

		/**
		 * 验证码的参数
		 */
		private String codeKey = CodeConstant.CODE_SMS_KEY;
		/**
		 * 验证码对应的值的参数
		 */
		private String codeValue = CodeConstant.CODE_SMS_VALUE;

		/**
		 * 获取验证码的长度,默认为4
		 * 
		 * @return
		 */
		public Integer getLength() {
			return length;
		}

		/**
		 * 设置验证码的长度,默认为4
		 * 
		 * @param length
		 */
		public void setLength(Integer length) {
			this.length = length;
		}

		/**
		 * 获取验证码的失效时间，默认为300s
		 * 
		 * @return
		 */
		public Integer getExpireIn() {
			return expireIn;
		}

		/**
		 * 设置验证码的失效时间，默认为60s
		 * 
		 * @param expireIn
		 */
		public void setExpireIn(Integer expireIn) {
			this.expireIn = expireIn;
		}

		/**
		 * 获取验证码是否包含字母,默认为true
		 * 
		 * @return
		 */
		public Boolean isContainLetter() {
			return isContainLetter;
		}

		/**
		 * 设置验证码是否包含字母,默认为true
		 * 
		 * @param isContainLetter
		 */
		public void setContainLetter(Boolean isContainLetter) {
			this.isContainLetter = isContainLetter;
		}

		/**
		 * 获取验证码是否包含数字,默认为true
		 * 
		 * @return
		 */
		public Boolean isContainNumber() {
			return isContainNumber;
		}

		/**
		 * 设置验证码是否包含数字,默认为true
		 * 
		 * @param isContainNumber
		 */
		public void setContainNumber(Boolean isContainNumber) {
			this.isContainNumber = isContainNumber;
		}

		/**
		 * 获取验证码的参数
		 * 
		 * @return
		 */
		public String getCodeKey() {
			return codeKey;
		}

		/**
		 * 设置验证码的参数
		 * 
		 * @param codeKey
		 */
		public void setCodeKey(String codeKey) {
			this.codeKey = codeKey;
		}

		/**
		 * 获取验证码对应的值的参数
		 */
		public String getCodeValue() {
			return codeValue;
		}

		/**
		 * 设置验证码对应的值的参数
		 * 
		 * @param codeValue
		 */
		public void setCodeValue(String codeValue) {
			this.codeValue = codeValue;
		}

		public SmsCodeProperties() {

		}

		public SmsCodeProperties(String codeKey, String codeValue) {
			this.codeKey = codeKey;
			this.codeValue = codeValue;
		}

		public SmsCodeProperties(Integer length, Integer expireIn, Boolean isContainLetter, Boolean isContainNumber,
				String codeKey, String codeValue) {
			this.length = length;
			this.expireIn = expireIn;
			this.isContainLetter = isContainLetter;
			this.isContainNumber = isContainNumber;
			this.codeKey = codeKey;
			this.codeValue = codeValue;
		}
	}

}
