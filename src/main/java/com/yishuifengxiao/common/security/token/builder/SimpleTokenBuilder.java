/**
 *
 */
package com.yishuifengxiao.common.security.token.builder;

import com.yishuifengxiao.common.security.constant.ErrorCode;
import com.yishuifengxiao.common.security.constant.TokenConstant;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.encoder.DES;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于Redis实现的token生成器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
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
     * @param username      用户名
     * @param deviceId      设备id
     * @param validSeconds  token的有效时间，单位为秒
     * @param preventsLogin 在达到最大的token数量限制时是否阻止后面的用户登陆
     * @param maxSessions   最大的token数量
     * @return SecurityToken 生成的token
     * @throws CustomException 生成时出现了问题
     */
    @Override
    public synchronized SecurityToken creatNewToken(String username, String deviceId, Integer validSeconds, boolean preventsLogin, int maxSessions) throws CustomException {
        if (StringUtils.isBlank(username)) {
            throw new CustomException(ErrorCode.USERNAME_NULL, "用户名不能为空");
        }

        deviceId = null == deviceId ? username : deviceId;

        if (maxSessions <= 0) {
            maxSessions = TokenConstant.MAX_SESSION_NUM;
        }

        // 先判断改token是否存在
        SecurityToken token = tokenHolder.get(username, deviceId);
        if (null != token) {
            // token已经存在
            if (token.isAvailable()) {
                // 当前token还有效

                // 重置有效期
                token.refreshExpireTime();
                // 更新token
                tokenHolder.save(token);
                return token;

            } else {
                // token已经失效,删除旧的token信息
                tokenHolder.delete(username, deviceId);
            }
        }

        return createNewToken(username, deviceId, validSeconds, preventsLogin, maxSessions);
    }

    /**
     * 新生成一个token
     *
     * @param username      用户账号
     * @param deviceId      设备id
     * @param validSeconds  token有效期
     * @param preventsLogin 是否阻止后面的用户登陆
     * @param maxSessions   最大登陆数量限制
     * @return
     * @throws CustomException
     */
    private SecurityToken createNewToken(String username, String deviceId, Integer validSeconds, boolean preventsLogin, int maxSessions) throws CustomException {
        // 先取出该用户所有可用的token
        List<SecurityToken> list = this.loadAllToken(username);

        if (null == list) {
            list = new ArrayList<>();
        }
        // 清除已过期的token
        list.stream().filter(v -> !v.isAvailable()).forEach(v -> tokenHolder.delete(v.getUsername(), v.getDeviceId()));
        // 所有激活状态的token
        List<SecurityToken> activeTokens = list.stream().filter(SecurityToken::isAvailable).collect(Collectors.toList());

        String tokenValue = DES.encrypt(new StringBuffer(username).append(TokenConstant.TOKEN_SPLIT_CHAR).append(deviceId).append(TokenConstant.TOKEN_SPLIT_CHAR).append(System.currentTimeMillis()).toString());

        SecurityToken newToken = new SecurityToken(tokenValue, username, deviceId, validSeconds);

        if (activeTokens.size() >= maxSessions) {
            if (preventsLogin) {
                throw new CustomException(ErrorCode.MAX_USER_LIMT, "已达到最大登陆用户数量限制");
            }
            // 将第一个token设置为失效状态
            SecurityToken existToken = list.get(0);
            existToken.setActive(false);
            // 覆盖掉旧的列表
            tokenHolder.save(existToken);
        }
        // 新增一个token
        tokenHolder.save(newToken);
        return newToken;
    }

    @Override
    public synchronized List<SecurityToken> loadAllToken(String username) {
        // 获取该用户所有的token信息
        return DataUtil.stream(tokenHolder.getAll(username)).filter(Objects::nonNull).sorted(Comparator.comparing(SecurityToken::getExpireAt)).collect(Collectors.toList());
    }


    private synchronized String[] parseTokenValue(String tokenValue) throws CustomException {

        if (StringUtils.isBlank(tokenValue)) {
            throw new CustomException(ErrorCode.TOKEN_VALUE_NULL, "非法的认证信息");
        }

        try {
            tokenValue = DES.decrypt(tokenValue);
        } catch (Exception e) {
            log.info("【yishuifengxiao-common-spring-boot-starter】解密tokenValue时出现问题，出现问题的原因为{}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
        }

        // 非法的tokenValue
        if (!StringUtils.contains(tokenValue, TokenConstant.TOKEN_SPLIT_CHAR)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
        }

        String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(tokenValue, TokenConstant.TOKEN_SPLIT_CHAR);
        // 非法的tokenValue
        if (null == tokens || tokens.length != TokenConstant.TOKEN_LENGTH) {
            throw new CustomException(ErrorCode.INVALID_TOKEN, "非法的认证信息");
        }

        return tokens;
    }

    @Override
    public synchronized SecurityToken loadByTokenValue(String tokenValue) throws CustomException {
        try {
            String[] tokens = this.parseTokenValue(tokenValue);
            return tokenHolder.get(tokens[0], tokens[1]);

        } catch (Exception e) {
            log.debug("【yishuifengxiao-common-spring-boot-starter】根据tokenValue获取token时出现问题，出现问题的原因为{}", e.getMessage());
        }

        return null;
    }

    @Override
    public synchronized void remove(SecurityToken token) throws CustomException {
        if (null == token) {
            return;
        }
        tokenHolder.delete(token.getUsername(), token.getDeviceId());
    }

    @Override
    public synchronized SecurityToken refreshExpireTime(SecurityToken token) throws CustomException {
        if (null != token && token.isAvailable()) {
            token = token.refreshExpireTime();
            // 更新token信息
            tokenHolder.save(token);
        }
        return token;
    }

    public TokenHolder getTokenHolder() {
        return tokenHolder;
    }

    public void setTokenHolder(TokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

}
