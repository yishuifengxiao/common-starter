package com.yishuifengxiao.common.support;

/**
 * 请求追踪上下文
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class TraceContext {
    /**
     * 请求追踪上下文缓存
     */
    private final static ThreadLocal<String> localVar = new ThreadLocal<>();

    /**
     * 保存请求追踪id
     *
     * @param traceId 请求追踪id
     */
    public static synchronized void set(String traceId) {
        localVar.set(traceId);
    }

    /**
     * 获取请求追踪id
     *
     * @return 请求追踪id
     */
    public static String get() {
        return localVar.get();
    }

    /**
     * 清空数据
     */
    public static synchronized void clear() {
        localVar.remove();
    }


}
