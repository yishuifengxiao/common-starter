/**
 * 
 */
package com.yishuifengxiao.common.security.token.builder;

import java.util.List;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * token生成器
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TokenBuilder {

	/**
	 * 创建一个新的token
	 * 
	 * @param username      用户名
	 * @param sessionId     会话id
	 * @param validSeconds  token的有效时间，单位为秒
	 * @param preventsLogin 在达到最大的token数量限制时是否阻止后面的用户登陆
	 * @param maxSessions   最大的token数量
	 * @return SecurityToken 生成的token
	 * @throws CustomException 生成时出现了问题
	 */
	SecurityToken creatNewToken(String username, String sessionId, Integer validSeconds, boolean preventsLogin,
			int maxSessions) throws CustomException;

	/**
	 * 获取所有的token
	 * 
	 * @param username        用户名
	 * @param isOnlyAvailable 是否仅返回可用的token的数量
	 * @return 所有的token,按照token的过期时间点从前到后排序
	 */
	List<SecurityToken> loadAllToken(String username, boolean isOnlyAvailable);

	/**
	 * 返回符合条件的token的数量
	 * 
	 * @param username  用户名
	 * @param isActive  token是否处于激活状态
	 * @param isExpired token是否已经过期
	 * @return 符合条件的token的数量
	 */
	int getTokenNum(String username, boolean isActive, boolean isExpired);

	/**
	 * <p>
	 * 从token值中解析出信息
	 * </p>
	 * 正确的tokenValue解析出来的后的信息为[username,clientId,currentTimeMillis]形式的数组
	 * 
	 * @param tokenValue token值
	 * @return [username,clientId,currentTimeMillis]形式的数组
	 * @throws CustomException 非法的token信息
	 */
	String[] parseTokenValue(String tokenValue) throws CustomException;

	/**
	 * 根据token的值获取token
	 * 
	 * @param tokenValue token的值
	 * @return SecurityToken 获取的token
	 * @throws CustomException 非法的token信息
	 */
	SecurityToken loadByTokenValue(String tokenValue) throws CustomException;

	/**
	 * 根据token的值从列表里移除一个token
	 * 
	 * @param tokenValue token的值
	 * @throws CustomException 非法的token信息
	 */
	void remove(String tokenValue) throws CustomException;

	/**
	 * 根据token的值重置token的过期时间点
	 * 
	 * @param tokenValue 令牌内容
	 * @return 重置后的token
	 * @throws CustomException 非法的token信息
	 */
	SecurityToken refreshExpireTime(String tokenValue) throws CustomException;

}
