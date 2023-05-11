package com.yishuifengxiao.common.security.token.holder.impl;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于redis的token存取工具类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class RedisTokenHolder implements TokenHolder {

    /**
     * redis中存储时的key的前缀
     */
    private final static String TOKEN_PREFIX = "security_token_store_redis";

    private RedisTemplate<String, Object> redisTemplate;

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
        List<SecurityToken> list = new ArrayList<>();
        Map<Object, Object> map = this.get(username).entries();
        if (null != map) {
            Iterator<Object> it = map.keySet().iterator();
            while (it.hasNext()) {
                Object val = it.next();
                if (null != val) {
                    list.add(this.get(username, val.toString()));
                }
            }
        }
        return DataUtil.stream(list).filter(Objects::nonNull).filter(t -> null != t.getExpireAt())
                .collect(Collectors.toList());
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
        try {
            this.delete(token.getUsername(), token.getDeviceId());
            this.get(token.getUsername()).put(token.getDeviceId(), token);
        } catch (Exception e) {
            log.info("【yishuifengxiao-common-spring-boot-starter】保存令牌{}时出现问题，失败的原因为 {}", token, e.getMessage());
            throw new CustomException(e.getMessage());
        }

    }


    /**
     * 根据用户账号和设备id删除一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     */
    @Override
    public synchronized void delete(String username, String deviceId) {
        this.get(username).delete(deviceId);
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
        Object data = this.get(username).get(deviceId);
        if (null == data || StringUtils.isBlank(data.toString())) {
            return null;
        }
        try {
            return (SecurityToken) data;
        } catch (Exception e) {
            log.info("【yishuifengxiao-common-spring-boot-starter】根据用户名{} 和会话{} 获取令牌时失败，失败的原因为 {}", username, deviceId, e.getMessage());
        }
        return null;
    }


    private BoundHashOperations<String, Object, Object> get(String key) {
        return redisTemplate.boundHashOps(new StringBuffer(TOKEN_PREFIX).append(":").append(key).toString());
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
        if (StringUtils.isBlank(token.getUsername())) {
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

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
