package com.yishuifengxiao.common.security.token.holder.impl;

import com.yishuifengxiao.common.security.token.SecurityToken;
import com.yishuifengxiao.common.security.token.holder.TokenHolder;
import com.yishuifengxiao.common.tool.collections.DataUtil;
import com.yishuifengxiao.common.tool.exception.CustomException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于内存的token存取工具类
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class InMemoryTokenHolder implements TokenHolder {

    private final Map<String, List<SecurityToken>> map = new HashMap<>();

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
        return DataUtil.stream(map.get(username)).filter(Objects::nonNull).filter(t -> null != t.getExpireAt())
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
        // 先删除
        this.delete(token.getName(), token.getDeviceId());
        List<SecurityToken> tokens = this.getAll(token.getName());
        tokens.add(token);
        map.remove(token.getName());
        map.put(token.getName(), tokens);

    }


    /**
     * 根据用户账号和设备id删除一个令牌
     *
     * @param username 用户账号
     * @param deviceId 设备id
     */
    @Override
    public synchronized void delete(String username, String deviceId) {
        List<SecurityToken> tokens = DataUtil.stream(this.getAll(username)).filter(Objects::nonNull)
                .filter(t -> !StringUtils.equalsIgnoreCase(t.getDeviceId(), deviceId)).collect(Collectors.toList());
        map.remove(username);
        map.put(username, tokens);
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
        List<SecurityToken> tokens = DataUtil.stream(this.getAll(username)).filter(Objects::nonNull)
                .filter(t -> StringUtils.equalsIgnoreCase(t.getDeviceId(), deviceId)).collect(Collectors.toList());
        return DataUtil.first(tokens);
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
