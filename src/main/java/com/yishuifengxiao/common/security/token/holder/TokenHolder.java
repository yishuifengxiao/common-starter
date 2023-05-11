/**
 *
 */
package com.yishuifengxiao.common.security.token.holder;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.tool.exception.CustomException;

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
     * 根据用户账号和设备id获取一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     * @return 令牌
     */
    SecurityToken get(String username, String deviceId);

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
     * 根据用户账号和设备id删除一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     */
    void delete(String username, String deviceId);


}
