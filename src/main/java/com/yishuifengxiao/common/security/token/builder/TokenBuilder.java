/**
 *
 */
package com.yishuifengxiao.common.security.token.builder;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

import java.util.List;

/**
 * token生成器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TokenBuilder {

    /**
     * <p>创建一个新的token</p>
     * <p>若存在旧的token则返回旧的token并自动续期</p>
     *
     * @param username      用户名
     * @param deviceId      设备id
     * @param validSeconds  token的有效时间，单位为秒
     * @param preventsLogin 在达到最大的token数量限制时是否阻止后面的用户登陆
     * @param maxSessions   最大的token数量
     * @return SecurityToken 生成的token
     * @throws CustomException 生成时出现了问题
     */
    SecurityToken creatNewToken(String username, String deviceId, Integer validSeconds, boolean preventsLogin,
                                int maxSessions) throws CustomException;

    /**
     * 获取所有的token
     *
     * @param username 用户名
     * @return 所有的token, 按照token的过期时间点从前到后排序
     */
    List<SecurityToken> loadAllToken(String username);


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
     * @param token 令牌
     * @throws CustomException 非法的token信息
     */
    void remove(SecurityToken token) throws CustomException;

    /**
     * 根据token的值重置token的过期时间点
     *
     * @param token 令牌
     * @return 重置后的token
     * @throws CustomException 非法的token信息
     */
    SecurityToken refreshExpireTime(SecurityToken token) throws CustomException;

}
