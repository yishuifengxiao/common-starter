package com.yishuifengxiao.common.security.support;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * token上下文存储
 */
public class SecurityHolder {
    /**
     * ThreadLocal
     */
    private final static ThreadLocal<AbstractAuthenticationToken> thread_local =
            new ThreadLocal<>();

    /**
     * 保存token
     *
     * @param token 待保存的token
     */
    public static void set(AbstractAuthenticationToken token) {
        if (null == token) {
            return;
        }
        thread_local.set(token);
    }

    /**
     * 获取存储的token
     *
     * @return 存储的token
     */
    public static AbstractAuthenticationToken get() {
        return thread_local.get();
    }

    /**
     * 清空存储的token
     */
    public static void clear() {
        thread_local.remove();
    }
}
