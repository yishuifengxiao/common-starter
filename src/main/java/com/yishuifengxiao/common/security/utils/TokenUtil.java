package com.yishuifengxiao.common.security.utils;

import com.yishuifengxiao.common.security.support.SecurityHelper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.SecurityValueExtractor;
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

	private static SecurityHelper securityHelper;

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

		return securityHelper.create(username, password, null);
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
		return create(username, password, deviceId);
	}

	/**
	 * 生成一个令牌
	 * 
	 * @param username  用户账号
	 * @param password  账号对应的密码
	 * @param deviceId 设备id
	 * @return 生成的令牌
	 * @throws CustomException 非法的用户信息或状态
	 */
	public static SecurityToken create(String username, String password, String deviceId) throws CustomException {

		return securityHelper.create(username, password, deviceId);
	}

	/**
	 * 生成一个令牌
	 * 
	 * @param username 用户账号
	 * @return 生成的令牌
	 * @throws CustomException 非法的用户信息或状态
	 */
	public static SecurityToken createUnsafe(String username) throws CustomException {

		return securityHelper.createUnsafe(username, null);
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
		return securityHelper.createUnsafe(username, deviceId);
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
	 * @param username  用户账号
	 * @param deviceId 设备id
	 * @return 生成的令牌
	 * @throws CustomException 非法的用户信息或状态
	 */
	public static SecurityToken createUnsafe(String username, String deviceId) throws CustomException {

		return securityHelper.createUnsafe(username, deviceId);
	}

	/**
	 * 生成一个令牌
	 * 
	 * @param username     用户账号
	 * @param deviceId    设备id
	 * @param validSeconds 令牌过期时间，单位为秒
	 * @return 生成的令牌
	 * @throws CustomException 非法的用户信息或状态
	 */
	public static SecurityToken createUnsafe(String username, String deviceId, int validSeconds)
			throws CustomException {

		return securityHelper.createUnsafe(username, deviceId, validSeconds);
	}

	public TokenUtil(SecurityHelper securityHelper, SecurityValueExtractor securityValueExtractor) {
		TokenUtil.securityHelper = securityHelper;
		TokenUtil.securityValueExtractor = securityValueExtractor;
	}

}
