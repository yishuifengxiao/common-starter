/**
 * 
 */
package com.yishuifengxiao.common.code.generator;

import com.yishuifengxiao.common.code.CodeProperties;
import com.yishuifengxiao.common.code.entity.ValidateCode;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * <p>
 * 验证码生成器
 * </p>
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CodeGenerator {

	/**
	 * 生成验证码
	 * 
	 * @param servletWebRequest 用户请求
	 * @param codeProperties    验证码属性配置
	 * @return 验证码
	 */
	ValidateCode generate(ServletWebRequest servletWebRequest, CodeProperties codeProperties);

	/**
	 * 生成验证码的唯一标识符
	 * 
	 * @param request        用户请求
	 * @param codeProperties 验证码属性配置
	 * @return 验证码的唯一标识符
	 * @throws CustomException  提取时出现问题
	 */
	String generateKey(ServletWebRequest request, CodeProperties codeProperties) throws CustomException ;

	/**
	 * 获取请求中携带的验证码的内容
	 * 
	 * @param request        用户请求
	 * @param codeProperties 验证码属性配置
	 * @return 验证码的内容
	 * @throws CustomException  提取时出现问题
	 */
	String getCodeInRequest(ServletWebRequest request, CodeProperties codeProperties) throws CustomException ;
}
