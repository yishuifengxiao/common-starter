/**
 *
 */
package com.yishuifengxiao.common.security.token.holder;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * token存取工具类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TokenHolder {

    /**
     * <p>
     * 根据用户账号获取所有的令牌
     * </p>
     * 按照令牌的过期时间点从小到到排列
     *
     * @param username 用户账号
     * @return 所有的令牌
     */
    List<SecurityToken> getAll(String username);

    /**
     * 保存一个令牌
     *
     * @param token 令牌
     * @throws CustomException 保存时出现问题
     */
    void save(SecurityToken token) throws CustomException;

    /**
     * 更新一个令牌
     *
     * @param token 令牌
     * @throws CustomException 更新时出现问题
     */
    void update(SecurityToken token) throws CustomException;

    /**
     * 根据用户账号和会话id删除一个令牌
     *
     * @param username  用户账号
     * @param sessionId 会话id
     * @throws CustomException 删除时出现问题
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
     * @throws CustomException  处理时出现问题
     */
    void setExpireAt(String username, LocalDateTime expireAt) throws CustomException;

}
