/**
 *
 */
package com.yishuifengxiao.common.security.token.builder;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.authentication.SimpleAuthority;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于Redis实现的token生成器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleTokenBuilder implements TokenBuilder {


    /**
     * token存储工具<br/>
     * 键：username<br/>
     * 值：该用户所有的token
     */
    private TokenHolder tokenHolder;


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
    @Override
    public synchronized SecurityToken createNewToken(Authentication authentication, String deviceId,
                                                     Integer validSeconds, boolean preventsLogin, int maxSessions,
                                                     Collection<? extends GrantedAuthority> authorities) throws CustomException {
        if (null == authentication) {
            throw new CustomException(ErrorCode.USERNAME_NULL, "用户名不能为空");
        }

        deviceId = null == deviceId ? authentication.getName() : deviceId;

        if (maxSessions <= 0) {
            maxSessions = TokenConstant.MAX_SESSION_NUM;
        }

        // 先判断改token是否存在
        SecurityToken token = tokenHolder.get(authentication.getName(), deviceId);
        if (null != token) {
            // token已经存在
            if (token.isAvailable()) {
                // 当前token还有效
                // 更新token
                return this.refreshExpireTime(token);

            } else {
                // token已经失效,删除旧的token信息
                tokenHolder.remove(token);
            }
        }

        return createNewToken(authentication.getName(), deviceId, validSeconds, preventsLogin, maxSessions,
                authorities);
    }

    /**
     * 新生成一个token
     *
     * @param username      用户账号
     * @param deviceId      设备id
     * @param validSeconds  token有效期
     * @param preventsLogin 是否阻止后面的用户登陆
     * @param maxSessions   最大登陆数量限制
     * @param authorities   authorities the collection of <tt>GrantedAuthority</tt>s for the principal represented by
     *                      this authentication object.
     * @return
     * @throws CustomException
     */
    @SuppressWarnings("unchecked")
	private SecurityToken createNewToken(String username, String deviceId, Integer validSeconds,
                                         boolean preventsLogin, int maxSessions, Collection<?
            extends GrantedAuthority> authorities) throws CustomException {
        // 先取出该用户所有可用的token
        List<SecurityToken> list =
                tokenHolder.getAll(username).stream().sorted(Comparator.comparing(SecurityToken::getExpireAt)).collect(Collectors.toList());

        if (null == list) {
            list = new ArrayList<>();
        }
        // 清除已过期的token
        list.stream().filter(v -> !v.isAvailable()).forEach(v -> tokenHolder.remove(v));
        // 所有激活状态的token
        List<SecurityToken> activeTokens =
                list.stream().filter(SecurityToken::isAvailable).collect(Collectors.toList());
        authorities = null == authorities ? Collections.EMPTY_LIST :
                authorities.stream().map(v -> new SimpleAuthority(v.getAuthority())).collect(Collectors.toList());
        SecurityToken newToken = new SecurityToken(username, deviceId, validSeconds, authorities);
        if (activeTokens.size() >= maxSessions) {
            if (preventsLogin) {
                throw new CustomException(ErrorCode.MAX_USER_LIMT, "已达到最大登陆用户数量限制");
            }
            // 将第一个token设置为失效状态
            SecurityToken existToken = list.get(0);
            tokenHolder.remove(existToken);
            existToken.setActive(false);
            // 覆盖掉旧的列表
            tokenHolder.save(existToken);
        }
        // 新增一个token
        tokenHolder.save(newToken);
        return newToken;
    }

    /**
     * 删除指定账号下所有的令牌
     *
     * @param authentication 用户认证信息
     */
    @Override
    public void clearAll(Authentication authentication) {
        tokenHolder.getAll(authentication.getName()).forEach(tokenHolder::remove);
    }

    @Override
    public void remove(SecurityToken token) {
        tokenHolder.remove(token);
    }


    /**
     * Read an access token from the store.
     *
     * @param tokenValue The token value.
     * @return The access token to read.
     */
    @Override
    public SecurityToken loadByTokenValue(String tokenValue) {
        return tokenHolder.loadByTokenValue(tokenValue);
    }

    /**
     * 重置token的有效时间
     *
     * @param token 待重置的token
     * @return 重置后的token
     */
    @Override
    public SecurityToken refreshExpireTime(SecurityToken token) throws CustomException {
        SecurityToken securityToken = token.refreshExpireTime();
        tokenHolder.remove(token);
        tokenHolder.save(securityToken);
        return securityToken;
    }

    public TokenHolder getTokenHolder() {
        return tokenHolder;
    }

    public void setTokenHolder(TokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

}
