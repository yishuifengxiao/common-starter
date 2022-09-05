package com.yishuifengxiao.common.core;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池生成器的默认实现
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class SimpleThreadPool implements ThreadPool {

    /**
     * 默认的线程池
     */
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            //
            Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 获取线程池
     *
     * @return 内置的线程池
     */
    @Override
    public ThreadPoolExecutor executor() {
        return this.executor;
    }

}
