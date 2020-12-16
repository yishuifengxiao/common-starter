/**
 * 
 */
package com.yishuifengxiao.common.code.extractor;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证码信息提取器
 * 
 * @author qingteng
 * @date 2020年11月7日
 * @version 1.0.0
 */
public interface CodeExtractor {

	/**
	 * 从请求中提取验证码的标识符<br/>
	 * <br/>
	 * 1. 对于短信验证码，一般来说标识符为发送目标的手机号<br/>
	 * 2. 对于邮件验证码，一般来说标识符为发送目标的邮箱地址<br/>
	 * 3. 对于图形验证码，一般来说为与用户约定的字符
	 * 
	 * @param request HttpServletRequest
	 * @param key     获取标识符的key
	 * @return 验证码对应的标识符
	 */
	String extractKey(HttpServletRequest request, String key);

	/**
	 * 获取验证码的内容
	 * 
	 * @param request HttpServletRequest
	 * @param key     获取验证码的内容的key
	 * @return 验证码的内容
	 */
	String extractValue(HttpServletRequest request, String key);
}
