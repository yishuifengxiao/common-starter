package com.yishuifengxiao.common.security.token.holder.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.bean.JsonUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.web.jackson2.WebJackson2Module;
import org.springframework.security.web.jackson2.WebServletJackson2Module;
import org.springframework.security.web.server.jackson2.WebServerJackson2Module;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基于redis的token存取工具类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class RedisTokenHolder implements TokenHolder {

    /**
     * redis中存储时的key的前缀
     */
    private final static String TOKEN_PREFIX = "security_token_store_redis::";
    /**
     * redis中存储值时的key的前缀
     */
    private final static String TOKEN_VAL_PREFIX = "security_token_store_redis_val::";

    private RedisTemplate<String, Object> redisTemplate;

    private final static ObjectMapper mapper = JsonUtil.mapper();

    static {
        try {
            // 启用反序列化所需的类型信息,在属性中添加@class
            mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY);
            mapper.registerModule(new CoreJackson2Module());
            mapper.registerModule(new WebJackson2Module());
            mapper.registerModule(new WebServletJackson2Module());
            mapper.registerModule(new WebServerJackson2Module());
            ClassLoader loader = RedisTokenHolder.class.getClassLoader();
//            List<com.fasterxml.jackson.databind.Module> modules =
//                    SecurityJackson2Modules.getModules(loader);
//            mapper.registerModules(modules);
        } catch (Exception e) {
        }


    }

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
        final Map<Object, Object> entries = this.token(username).entries();
        try {
            return entries.values().stream().filter(Objects::nonNull).map(v -> (SecurityToken) v).collect(Collectors.toList());
        } catch (Exception e) {
        }
        return Collections.emptyList();

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
        this.remove(token);
        //有效时间
        LocalDateTime expiredAtTime = token.getExpireAt();
        long untiled = LocalDateTime.now().until(expiredAtTime, ChronoUnit.SECONDS);

        this.token(token.getName()).put(token.getDeviceId(), token);
        this.token(token.getName()).expire(untiled, TimeUnit.SECONDS);
        this.tokenVal(token.getValue()).set(token, untiled, TimeUnit.SECONDS);
    }


    /**
     * 删除指定的令牌
     *
     * @param token 令牌
     * @throws CustomException 删除时出现问题
     */
    @Override
    public synchronized void remove(SecurityToken token) {
        this.tokenVal(token.getValue()).getAndDelete();
        this.token(token.getName()).delete(token.getDeviceId());
    }

    @Override
    public SecurityToken loadByTokenValue(String tokenValue) {
        Object val = this.tokenVal(tokenValue).get();
        return val instanceof SecurityToken ? (SecurityToken) val : null;
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
        Object val = this.token(username).get(deviceId);
        return val instanceof SecurityToken ? (SecurityToken) val : null;
    }


    private synchronized BoundHashOperations<String, Object, Object> token(String key) {
        return redisTemplate.boundHashOps(new StringBuffer(TOKEN_PREFIX).append(key).toString());
    }

    private synchronized BoundValueOperations<String, Object> tokenVal(String key) {
        return redisTemplate.boundValueOps(new StringBuffer(TOKEN_VAL_PREFIX).append(key).toString());
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
        if (null == token.getName() || StringUtils.isBlank(token.getName())) {
            throw new CustomException("令牌中必须包含用户账号信息");
        }
        if (StringUtils.isBlank(token.getDeviceId())) {
            throw new CustomException("令牌中必须包含请求识别信息");
        }
        if (null == token.getExpireAt()) {
            throw new CustomException("令牌中必须包含过期时间信息");
        }
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
