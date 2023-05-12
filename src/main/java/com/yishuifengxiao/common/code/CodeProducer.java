/**
 * 
 */
package com.yishuifengxiao.common.code;

import org.springframework.web.context.request.ServletWebRequest;

import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.code.eunm.CodeType;
import com.yishuifengxiao.common.tool.exception.CustomException ;

/**
 * 验证码处理器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CodeProducer {
	/**
	 * 创建校验码
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException  创建或发送验证码时出现问题
	 */
	ValidateCode create(ServletWebRequest request, CodeType codeType) throws CustomException ;

	/**
	 * <p>
	 * 创建校验码
	 * </p>
	 * 
	 * 生成验证码存储时的key的含义如下:
	 * <ul>
	 * <li>对于短信验证码,key为手机号</li>
	 * <li>对于邮件验证码，key为邮箱</li>
	 * <li>对于图像验证码，key为sessionid或指定的值</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param key      验证码的发送目标
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException  创建或发送验证码时出现问题
	 */
	ValidateCode create(ServletWebRequest request, String key, CodeType codeType) throws CustomException ;
	/**
	 * 创建校验码并发送验证码
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException  创建或发送验证码时出现问题
	 */
	ValidateCode createAndSend(ServletWebRequest request, CodeType codeType) throws CustomException ;

	/**
	 * <p>
	 * 创建校验码并发送验证码
	 * </p>
	 * 
	 * 生成验证码存储时的key的含义如下:
	 * <ul>
	 * <li>对于短信验证码,key为手机号</li>
	 * <li>对于邮件验证码，key为邮箱</li>
	 * <li>对于图像验证码，key为sessionid或指定的值</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param key      验证码的发送目标
	 * @param codeType 验证码的类型
	 * @return 创建并发送的验证码
	 * @throws CustomException  创建或发送验证码时出现问题
	 */
	ValidateCode createAndSend(ServletWebRequest request, String key, CodeType codeType) throws CustomException ;

	/**
	 * <p>
	 * 校验用户请求中携带的验证码是否正确
	 * </p>
	 * 在验证码不匹配时会抛出异常，主要异常场景有:
	 * <ul>
	 * <li>验证码不存在</li>
	 * <li>存储的验证码已过期</li>
	 * <li>存储的验证码与给定的验证码不匹配</li>
	 * </ul>
	 * 
	 * @param request  用户请求
	 * @param codeType 验证码类型
	 * @throws CustomException  验证码不匹配或已过期等未通过验证时
	 */
	void validate(ServletWebRequest request, CodeType codeType) throws CustomException ;

	/**
	 * <p>
	 * 根据验证码的唯一标识符判断给定的验证码是否正确
	 * </p>
	 * 在验证码不匹配时会抛出异常，主要异常场景有:
	 * <ul>
	 * <li>验证码不存在</li>
	 * <li>存储的验证码已过期</li>
	 * <li>存储的验证码与给定的验证码不匹配</li>
	 * </ul>
	 * 
	 * @param key           验证码的唯一标识符
	 * @param codeInRequest 给定的验证码
	 * @throws CustomException  验证码不匹配或已过期等未通过验证时
	 */
	void validate(String key, String codeInRequest) throws CustomException ;
}
