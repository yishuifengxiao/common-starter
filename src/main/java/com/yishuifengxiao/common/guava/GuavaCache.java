package com.yishuifengxiao.common.guava;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * <p>
 * 基于guva实现的定时缓存
 * </p>
 * <p>
 * 该缓存最大能容纳1000组数据，在达到最大数据容量之后，加入某组数据超过10h没有被使用过，该数据会被释放掉，不再被存储
 * </p>
 * 
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public class GuavaCache {

	private static final Cache<String, Object> GUAVA_CACHE = CacheBuilder.newBuilder().maximumSize(1000)
			.expireAfterAccess(24, TimeUnit.HOURS).build();

	/**
	 * 在当前线程里存储一组键值对
	 * 
	 * @param key   存储的key，不能为null
	 * @param value 被保存的数据，不能为null
	 */
	public static synchronized void putLocal(String key, Object value) {
		if (null == key || null == value) {
			return;
		}
		put(new StringBuffer(key.trim()).append(Thread.currentThread().getId()).toString(), value);
	}

	/**
	 * 根据指定的key从当前线程里获取一个存储的数据
	 * 
	 * @param key 存储的key，不能为null
	 * @return 存储的数据，数据不存在时值为null
	 */
	public static synchronized Object getLocal(String key) {
		if (null == key) {
			return null;
		}
		return get(new StringBuffer(key.trim()).append(Thread.currentThread().getId()).toString());
	}

	/**
	 * 存储一组键值对
	 * 
	 * @param key   存储的key，不能为null
	 * @param value 被保存的数据，不能为null
	 */
	public static synchronized void put(String key, Object value) {
		if (null == key || null == value) {
			return;
		}
		GUAVA_CACHE.put(key.trim(), value);
	}

	/**
	 * 根据指定的key获取一个存储的数据
	 * 
	 * @param key 存储的key，不能为null
	 * @return 存储的数据，数据不存在时值为null
	 */
	public static synchronized Object get(String key) {
		if (null == key) {
			return null;
		}
		return GUAVA_CACHE.getIfPresent(key.trim());
	}

	/**
	 * 清空所有的缓存数据
	 */
	public synchronized static void clearAll() {
		GUAVA_CACHE.invalidateAll();
	}

	/**
	 * 根据存储的键移除指定的数据
	 * 
	 * @param key 存储的键
	 */
	public synchronized static void remove(String key) {
		if (null == key) {
			return;
		}
		GUAVA_CACHE.invalidate(key.trim());
	}

}
