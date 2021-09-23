/**
 * 
 */
package com.yishuifengxiao.common.code;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.yishuifengxiao.common.code.constant.Constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码属性配置
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "yishuifengxiao.code")
public class CodeProperties {
	/**
	 * 是否开启验证码功能，默认为关闭
	 */
	protected Boolean enable = false;
	/**
	 * 将验证码存储到Redis时的key的前缀，默认值为 validate_code_
	 */
	protected String prefix = Constant.PREFIX;

	/**
	 * 是否显示加载日志，默认为false
	 */
	private Boolean showDeatil = false;

	/**
	 * 是否在验证成功后删除验证过的验证码
	 */
	protected Boolean deleteOnSuccess = true;
	/**
	 * 图形验证码相关的配置
	 */
	protected ImageCodeProperties image = new ImageCodeProperties();
	/**
	 * 短信验证码相关的配置
	 */
	protected SmsCodeProperties sms = new SmsCodeProperties();
	/**
	 * 邮箱验证码的相关配置
	 */
	protected EmailCodeProperties email = new EmailCodeProperties();

	/**
	 * 短信验证码的配置参数
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class SmsCodeProperties {
		/**
		 * 验证码的长度,默认为4
		 */
		protected Integer length = Constant.DEFAULT_CODE_LENGTH;
		/**
		 * 验证码的失效时间，单位秒，默认为300s
		 */
		protected Integer expireIn = Constant.DEFAULT_EXPIREIN;
		/**
		 * 验证码是否包含字母,默认不包含
		 */
		protected Boolean containLetter = Constant.IS_CONTAIN_LETTERS;
		/**
		 * 验证码是否包含数字,默认包含
		 */
		protected Boolean containNumber = Constant.IS_CONTAIN_NUMBERS;

		/**
		 * 从请求中获取短信验证码的发送目标(手机号)的参数，默认值为 phone
		 */
		protected String codeKey = Constant.CODE_SMS_KEY;
		/**
		 * 请求中获取短信验证码对应的短信内容的参数，默认值为 phone_code
		 */
		protected String codeValue = Constant.CODE_SMS_VALUE;

		public Integer getLength() {
			return length;
		}

		public void setLength(Integer length) {
			this.length = length;
		}

		public Integer getExpireIn() {
			return expireIn;
		}

		public void setExpireIn(Integer expireIn) {
			this.expireIn = expireIn;
		}

		public Boolean getContainLetter() {
			return containLetter;
		}

		public void setContainLetter(Boolean containLetter) {
			this.containLetter = containLetter;
		}

		public Boolean getContainNumber() {
			return containNumber;
		}

		public void setContainNumber(Boolean containNumber) {
			this.containNumber = containNumber;
		}

		public String getCodeKey() {
			return codeKey;
		}

		public void setCodeKey(String codeKey) {
			this.codeKey = codeKey;
		}

		public String getCodeValue() {
			return codeValue;
		}

		public void setCodeValue(String codeValue) {
			this.codeValue = codeValue;
		}

	}

	/**
	 * 邮件验证码相关的配置
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class EmailCodeProperties extends SmsCodeProperties {

		/**
		 * 验证码邮箱的内容模板
		 */
		protected String contentTemplate = "您的验证码的内容为{0} ,验证码的有效时间为 {1} 秒";

		/**
		 * 验证码邮箱的标题
		 */
		protected String title = "帐号保护验证";

		/**
		 * 从请求中获取邮件验证码的邮箱的参数，默认值为 email
		 */
		protected String codeKey = Constant.CODE_EMAIL_KEY;
		/**
		 * 请求中获取邮件验证码对应的值的参数，默认值为 email_code
		 */
		protected String codeValue = Constant.CODE_EMAIL_VALUE;

		public EmailCodeProperties() {
			this.setExpireIn(60 * 30);
		}

		public String getContentTemplate() {
			return contentTemplate;
		}

		public void setContentTemplate(String contentTemplate) {
			this.contentTemplate = contentTemplate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String getCodeKey() {
			return codeKey;
		}

		@Override
		public void setCodeKey(String codeKey) {
			this.codeKey = codeKey;
		}

		@Override
		public String getCodeValue() {
			return codeValue;
		}

		@Override
		public void setCodeValue(String codeValue) {
			this.codeValue = codeValue;
		}

	}

	/**
	 * 图形验证码的参数配置
	 * 
	 * @author yishui
	 * @version 1.0.0
	 * @since 1.0.0
	 */

	public static class ImageCodeProperties extends SmsCodeProperties {
		/**
		 * 验证码的宽度,默认为70
		 */
		protected Integer width = Constant.DEFAULT_IMAGE_CODE_WIDTH;
		/**
		 * 验证码的高度,默认为 28
		 */
		protected Integer height = Constant.DEFAULT_IMAGE_CODE_HEIGHT;

		/**
		 * 是否生成干扰条纹背景，默认为false
		 */
		protected Boolean fringe = Constant.IS_FRINGE;

		/**
		 * 从请求中获取图形验证码的标识符的参数，默认值为 image
		 */
		protected String codeKey = Constant.CODE_IMAGE_KEY;
		/**
		 * 请求中获取邮件验证码对应的值的参数，默认值为image_code
		 */
		protected String codeValue = Constant.CODE_IMAGE_VALUE;

		public Integer getWidth() {
			return width;
		}

		public void setWidth(Integer width) {
			this.width = width;
		}

		public Integer getHeight() {
			return height;
		}

		public void setHeight(Integer height) {
			this.height = height;
		}

		public Boolean getFringe() {
			return fringe;
		}

		public void setFringe(Boolean fringe) {
			this.fringe = fringe;
		}

		@Override
		public String getCodeKey() {
			return codeKey;
		}

		@Override
		public void setCodeKey(String codeKey) {
			this.codeKey = codeKey;
		}

		@Override
		public String getCodeValue() {
			return codeValue;
		}

		@Override
		public void setCodeValue(String codeValue) {
			this.codeValue = codeValue;
		}

	}

}
