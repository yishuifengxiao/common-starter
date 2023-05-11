package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.tool.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全信息处理器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AuthorizeHelper {
    

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
