package com.yishuifengxiao.common.security.support;

import com.yishuifengxiao.common.security.token.SecurityToken;

/**
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SecurityContext {

    private final static ThreadLocal<SecurityToken> THREAD_LOCAL = new ThreadLocal<>();


    /**
     * 向ThreadLocal存放SecurityToken数据
     *
     * @param token SecurityToken 信息
     */
    public synchronized static void set(SecurityToken token) {
        THREAD_LOCAL.set(token);
    }

    /**
     * 获取ThreadLocal中的SecurityToken数据
     */
    public synchronized static void get() {
        THREAD_LOCAL.get();
    }

    /**
     * 移除ThreadLocal中的SecurityToken数据
     */
    public synchronized static void remove() {
        THREAD_LOCAL.remove();
    }

}
