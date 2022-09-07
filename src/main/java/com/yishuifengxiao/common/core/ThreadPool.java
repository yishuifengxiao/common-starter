package com.yishuifengxiao.common.core;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池生成器
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ThreadPool {

	/**
	 * 获取一个自定义ThreadPoolExecutor
	 *
	 * @return 自定义ThreadPoolExecutor
	 */
	ThreadPoolExecutor executor();

}
