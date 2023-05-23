package com.yishuifengxiao.common.security.token.holder.impl;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于内存的token存取工具类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InMemoryTokenHolder implements TokenHolder {

    /**
     * 存储用户所有的令牌
     */
    private final Map<String, List<SecurityToken>> map = new ConcurrentHashMap<>();

    /**
     * key : SecurityToken tokenVal
     * value :SecurityToken
     */
    private final Map<String, SecurityToken> tokenValMap = new ConcurrentHashMap<>();

    /**
     * <p>
     * 根据用户账号获取所有的令牌
     * </p>
     * 按照令牌的过期时间点从小到到排列
     *
     * @param username 用户账号
     * @return 所有的令牌
     */
    @Override
    public synchronized List<SecurityToken> getAll(String username) {
        return map.getOrDefault(username, Collections.emptyList());
    }

    /**
     * 保存一个令牌
     *
     * @param token 令牌
     * @throws CustomException 保存时出现问题
     */
    @Override
    public synchronized void save(SecurityToken token) throws CustomException {
        this.check(token);
        tokenValMap.put(token.getValue(), token);
        List<SecurityToken> tokens = map.getOrDefault(token.getName(), Collections.emptyList())
                .stream().filter(v -> !StringUtils.equals(v.getDeviceId(), token.getDeviceId())).collect(Collectors.toList());
        tokens.add(token);
        map.put(token.getName(), tokens);

    }


    /**
     * 删除指定的令牌
     *
     * @param token 令牌
     */
    @Override
    public synchronized void remove(SecurityToken token) {

        List<SecurityToken> tokens = map.getOrDefault(token.getName(), Collections.emptyList())
                .stream().filter(v -> !StringUtils.equals(v.getDeviceId(), token.getDeviceId())).collect(Collectors.toList());
        tokenValMap.put(token.getValue(), null);
        map.put(token.getName(), tokens);
    }

    @Override
    public SecurityToken loadByTokenValue(String tokenValue) {
        SecurityToken token = tokenValMap.get(tokenValue);
        return token;
    }

    /**
     * 根据用户账号和设备id获取一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     * @return 令牌
     */
    @Override
    public synchronized SecurityToken get(String username, String deviceId) {
        return this.getAll(username).stream().filter(t -> StringUtils.equals(t.getDeviceId(), deviceId)).findFirst().orElse(null);
    }


    /**
     * 检查令牌的内容合法性
     *
     * @param token 令牌
     * @throws CustomException 令牌非法
     */
    private void check(SecurityToken token) throws CustomException {
        if (null == token) {
            throw new CustomException("令牌不能为空");
        }
        if (StringUtils.isBlank(token.getName())) {
            throw new CustomException("令牌中必须包含用户账号信息");
        }
        if (StringUtils.isBlank(token.getDeviceId())) {
            throw new CustomException("令牌中必须包含请求识别信息");
        }
        if (null == token.getExpireAt()) {
            throw new CustomException("令牌中必须包含过期时间信息");
        }
        if (null == token.getValidSeconds()) {
            throw new CustomException("令牌中必须包含有效时间信息");
        }
    }

}
