package com.yishuifengxiao.common.security.httpsecurity;

import com.yishuifengxiao.common.security.exception.AbnormalAccountException;
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
     * 根据用户账号获取用户账号信息
     *
     * @param username 用户账号
     * @return 用户账号信息
     * @throws AbnormalAccountException 非法的用户账号
     */
    UserDetails loadUserByUsername(String username) throws AbnormalAccountException;

}
