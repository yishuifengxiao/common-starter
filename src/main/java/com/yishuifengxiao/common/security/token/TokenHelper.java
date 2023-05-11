package com.yishuifengxiao.common.security.token;

import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * 安全信息处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TokenHelper {

    /**
     * 根据用户账号创建一个不安全的令牌
     *
     * @param username 用户账号
     * @param deviceId 用户设备id
     * @return 不安全的令牌
     * @throws CustomException 创建令牌时出现问题
     */
    SecurityToken createUnsafe(String username, String deviceId) throws CustomException;

    /**
     * 根据用户账号创建一个不安全的令牌
     *
     * @param username     用户账号
     * @param deviceId     用户设备id
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 不安全的令牌
     * @throws CustomException 创建令牌时出现问题
     */
    SecurityToken createUnsafe(String username, String deviceId, int validSeconds) throws CustomException;

    /**
     * 根据用户账号创建一个安全的令牌
     *
     * @param username 用户账号
     * @param password 用户密码
     * @param deviceId 用户设备id
     * @return 安全的令牌
     * @throws CustomException 创建令牌时出现问题
     */
    SecurityToken create(String username, String password, String deviceId) throws CustomException;

    /**
     * 删除token
     *
     * @param token 待删除的token
     * @throws CustomException 删除令牌时出现问题
     */
    void remove(SecurityToken token) throws CustomException;

}
