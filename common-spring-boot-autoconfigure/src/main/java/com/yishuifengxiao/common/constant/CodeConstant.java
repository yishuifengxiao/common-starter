/**
 * 
 */
package com.yishuifengxiao.common.constant;

/**
 * 与验证码相关的常量
 * 
 * @author yishui
 * @date 2019年1月22日
 * @version v1.0.0
 */
public class CodeConstant {
	/**
	 * 短信验证码标志
	 */
	public final static String DEFAULT_PARAMETER_NAME_CODE_SMS = "sms";
	/**
	 * 图形验证码标志
	 */
	public final static String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "image";
	/**
	 * 邮箱验证码的标志
	 */
	public final static String DEFAULT_PARAMETER_NAME_CODE_EMAIL = "email";
	/**
	 * 默认的手机号码参数
	 */
	public final static String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";
	/**
	 * 默认的短信验证码宽度
	 */
	public final static int DEFAULT_IMAGE_CODE_WIDTH = 70;
	/**
	 * 默认的短信验证码的高度
	 */
	public final static int DEFAULT_IMAGE_CODE_HEIGHT = 28;
	/**
	 * 默认的验证码的长度
	 */
	public final static int DEFAULT_CODE_LENGTH = 4;
	/**
	 * 默认的验证码的有效期，单位为秒
	 */
	public final static int DEFAULT_EXPIREIN = 60;
	/**
	 * 验证码是否包含字母
	 */
	public final static boolean IS_CONTAIN_LETTERS = true;
	/**
	 * 验证码是否包含数字
	 */
	public final static boolean IS_CONTAIN_NUMBERS = true;
	/**
	 * 图形验证码的key的值
	 */
	public final static String CODE_IMAGE_KEY = "image";
	
	/**
	 * 图形验证码的key的对应的值
	 */
	public final static String CODE_IMAGE_VALUE = "image_code";
	/**
	 * 短信验证码的key的值
	 */
	public final static String CODE_SMS_KEY = "phone";
	
	/**
	 * 短信验证码的key的对应的值
	 */
	public final static String CODE_SMS_VALUE = "phone_code";
	
	/**
	 * 邮箱验证码的key的值
	 */
	public final static String CODE_EMAIL_KEY = "email";
	
	/**
	 * 邮箱验证码的key的对应的值
	 */
	public final static String CODE_EMAIL_VALUE = "email_code";
}
