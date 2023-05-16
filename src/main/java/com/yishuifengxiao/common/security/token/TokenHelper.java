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
    SecurityToken create(String username, String deviceId, String password) throws CustomException;


    /**
     * <p>清除指定账号下面的所有的令牌</p>
     * <p>一般用于用户修改密码后使用</p>
     *
     * @param username 用户账号
     * @throws CustomException 清除令牌时出现问题
     */
    void clear(String username) throws CustomException;

}
