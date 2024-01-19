/**
 *
 */
package com.yishuifengxiao.common.security.token.builder;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.springframework.security.core.Authentication;

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
     * @param authentication 用户认证信息
     * @param deviceId       设备id
     * @param validSeconds   token的有效时间，单位为秒
     * @param preventsLogin  在达到最大的token数量限制时是否阻止后面的用户登陆
     * @param maxSessions    最大的token数量
     * @param authorities    authorities the collection of <tt>GrantedAuthority</tt>s for the principal represented by
     *                       this authentication object.
     * @return SecurityToken 生成的token
     * @throws CustomException 生成时出现了问题
     */
    SecurityToken createNewToken(Authentication authentication, String deviceId, Integer validSeconds,
                                 boolean preventsLogin, int maxSessions) throws CustomException;

    /**
     * 删除指定账号下所有的令牌
     *
     * @param authentication 用户认证信息
     */
    void clearAll(Authentication authentication);

    /**
     * 删除指定的令牌
     *
     * @param token 待删除的令牌
     */
    void remove(SecurityToken token);

    /**
     * Read an access token from the store.
     *
     * @param tokenValue The token value.
     * @return The access token to read.
     */
    SecurityToken loadByTokenValue(String tokenValue);

    /**
     * 重置token的有效时间
     *
     * @param token 待重置的token
     * @return 重置后的token
     * @throws CustomException 刷新时出现了问题
     */
    SecurityToken refreshExpireTime(SecurityToken token) throws CustomException;

}
