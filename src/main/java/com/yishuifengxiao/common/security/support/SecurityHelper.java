package com.yishuifengxiao.common.security.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

/**
 * 安全信息处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SecurityHelper {

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
     * 根据令牌内容获取认证信息
     *
     * @param tokenValue 令牌内容
     * @return 认证信息
     * @throws CustomException 非法的令牌
     */
    Authentication authorize(String tokenValue) throws CustomException;

    /**
     * 根据用户账号获取用户账号信息
     *
     * @param username 用户账号
     * @param password 密码
     * @return 用户账号信息
     * @throws CustomException 非法的用户账号或密码
     */
    UserDetails authorize(String username, String password) throws CustomException;

    /**
     * 根据用户账号获取用户账号信息
     *
     * @param username 用户账号
     * @return 用户账号信息
     * @throws CustomException 非法的用户账号
     */
    UserDetails loadUserByUsername(String username) throws CustomException;

}
