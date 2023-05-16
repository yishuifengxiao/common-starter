package com.yishuifengxiao.common.security.utils;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.TokenHelper;
import com.yishuifengxiao.common.security.token.extractor.SecurityValueExtractor;
import com.yishuifengxiao.common.tool.exception.CustomException;

import javax.servlet.http.HttpServletRequest;

/**
 * 令牌生成工具
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TokenUtil {

    private static TokenHelper tokenHelper;

    private static SecurityValueExtractor securityValueExtractor;

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param password 账号对应的密码
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(String username, String password) throws CustomException {

        return tokenHelper.create(username, null, password);
    }

    /**
     * 生成一个令牌
     *
     * @param request  HttpServletRequest
     * @param username 用户账号
     * @param password 账号对应的密码
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(HttpServletRequest request, String username, String password)
            throws CustomException {
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return create(username, deviceId, password);
    }

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param password 账号对应的密码
     * @param deviceId 设备id
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken create(String username, String deviceId, String password) throws CustomException {

        return tokenHelper.create(username, deviceId, password);
    }

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username) throws CustomException {

        return tokenHelper.createUnsafe(username, null);
    }

    /**
     * 生成一个令牌
     *
     * @param request  HttpServletRequest
     * @param username 用户账号
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(HttpServletRequest request, String username) throws CustomException {
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return tokenHelper.createUnsafe(username, deviceId);
    }

    /**
     * 生成一个令牌
     *
     * @param username     用户账号
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, int validSeconds) throws CustomException {
        return createUnsafe(username, null, validSeconds);
    }

    /**
     * 生成一个令牌
     *
     * @param request      HttpServletRequest
     * @param username     用户账号
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(HttpServletRequest request, String username, int validSeconds)
            throws CustomException {
        String deviceId = securityValueExtractor.extractDeviceId(request, null);
        return createUnsafe(username, deviceId, validSeconds);
    }

    /**
     * 生成一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, String deviceId) throws CustomException {

        return tokenHelper.createUnsafe(username, deviceId);
    }

    /**
     * 生成一个令牌
     *
     * @param username     用户账号
     * @param deviceId     设备id
     * @param validSeconds 令牌过期时间，单位为秒
     * @return 生成的令牌
     * @throws CustomException 非法的用户信息或状态
     */
    public static SecurityToken createUnsafe(String username, String deviceId, int validSeconds)
            throws CustomException {

        return tokenHelper.createUnsafe(username, deviceId, validSeconds);
    }


    /**
     * 清除所有的登录信息
     *
     * @param username 用户账号
     * @throws CustomException 清除所有的登录信息时出现问题
     */
    public static void clearAuthentication(String username) throws CustomException {
        tokenHelper.clear(username);
    }

    public TokenUtil(TokenHelper tokenHelper, SecurityValueExtractor securityValueExtractor) {
        TokenUtil.tokenHelper = tokenHelper;
        TokenUtil.securityValueExtractor = securityValueExtractor;
    }

}
