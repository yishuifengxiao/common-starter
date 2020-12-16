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
 * @author qingteng
 * @date 2020年11月29日
 * @version 1.0.0
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
	 * @return SecurityToken
	 * @throws CustomException
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
	 * 从token值中解析出信息<br/>
	 * 正确的tokenValue解析出来的后的信息为[username,clientId,currentTimeMillis]形式的数组
	 * 
	 * @param tokenValue token值
	 * @return [username,clientId,currentTimeMillis]形式的数组
	 * @throws CustomException
	 */
	String[] parseTokenValue(String tokenValue) throws CustomException;

	/**
	 * 根据token的值获取token
	 * 
	 * @param tokenValue token的值
	 * @return SecurityToken
	 * @throws CustomException
	 */
	SecurityToken loadByTokenValue(String tokenValue) throws CustomException;

	/**
	 * 根据token的值从列表里移除一个token
	 * 
	 * @param tokenValue token的值
	 * @throws CustomException 
	 */
	void remove(String tokenValue) throws CustomException;

	/**
	 * 根据token的值重置token的过期时间点
	 * 
	 * @param tokenValue token的值
	 * @throws CustomException
	 */
	SecurityToken refreshExpireTime(String tokenValue) throws CustomException;

}
