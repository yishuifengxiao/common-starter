/**
 * 
 */
package com.yishuifengxiao.common.security.token.holder;

import java.time.LocalDateTime;
import java.util.List;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;
import com.yishuifengxiao.common.tool.exception.ValidateException;

/**
 * token存取工具类
 * 
 * @author qingteng
 * @date 2020年11月29日
 * @version 1.0.0
 */
public interface TokenHolder {

	/**
	 * 获取所有的token<br/>
	 * 按照token的过期时间点从小到到排列
	 * 
	 * @param key
	 * @return
	 */
	List<SecurityToken> getAll(String key);

	/**
	 * 保存一个令牌
	 * 
	 * @param token
	 * @throws CustomException
	 */
	void save(SecurityToken token) throws CustomException;
	

	/**
	 * 更新一个令牌
	 * 
	 * @param token
	 * @throws CustomException
	 */
	void update(SecurityToken token) throws CustomException;

	/**
	 * 根据用户账号和会话id删除一个令牌
	 * 
	 * @param username  用户账号
	 * @param sessionId 会话id
	 * @throws CustomException
	 */
	void delete(String username, String sessionId) throws CustomException;

	/**
	 * 根据用户账号和会话id获取一个令牌
	 * 
	 * @param username  用户账号
	 * @param sessionId 会话id
	 * @return 令牌
	 */
	SecurityToken get(String username, String sessionId);
	
	/**
	 * 设置过期时间点
	 * 
	 * @param username 用户账号
	 * @param expireAt 过期时间点
	 * @throws ValidateException
	 */
	void setExpireAt(String username,LocalDateTime expireAt) throws ValidateException;

}
