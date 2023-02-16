package com.yishuifengxiao.common.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

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

    private static final Cache<String, Object> GUAVA_CACHE = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(24, TimeUnit.HOURS).build();
    

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
     * 在缓存中放入一个数据，数据的键为<code>value.getClass().getName()</code>
     *
     * @param value
     */
    public static synchronized void put(Object value) {
        if (null == value) {
            return;
        }
        GUAVA_CACHE.put(value.getClass().getName(), value);
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
     * Returns the value associated with {@code key} in this cache, obtaining that value from {@code
     * loader} if necessary. The method improves upon the conventional "if cached, return; otherwise
     * create, cache and return" pattern. For further improvements, use {@link LoadingCache} and its
     * {@link LoadingCache#get(Object) get(K)} method instead of this one.
     *
     * <p>Among the improvements that this method and {@code LoadingCache.get(K)} both provide are:
     *
     * <ul>
     * <li>{@linkplain LoadingCache#get(Object) awaiting the result of a pending load} rather than
     *     starting a redundant one
     * <li>eliminating the error-prone caching boilerplate
     * </ul>
     *
     * <p>Among the further improvements that {@code LoadingCache} can provide but this method cannot:
     *
     * <ul>
     * <li>consolidation of the loader logic to {@linkplain CacheBuilder#build(CacheLoader) a single
     *     authoritative location}
     * <li>{@linkplain LoadingCache#refresh refreshing of entries}, including {@linkplain
     *     CacheBuilder#refreshAfterWrite automated refreshing}
     * <li>{@linkplain LoadingCache#getAll bulk loading requests}, including {@linkplain
     *     CacheLoader#loadAll bulk loading implementations}
     * </ul>
     *
     * <p><b>Warning:</b> For any given key, every {@code loader} used with it should compute the same
     * value. Otherwise, a call that passes one {@code loader} may return the result of another call
     * with a differently behaving {@code loader}. For example, a call that requests a short timeout
     * for an RPC may wait for a similar call that requests a long timeout, or a call by an
     * unprivileged user may return a resource accessible only to a privileged user making a similar
     * call. To prevent this problem, create a key object that includes all values that affect the
     * result of the query. Or use {@code LoadingCache.get(K)}, which lacks the ability to refer to
     * state other than that in the key.
     *
     * <p><b>Warning:</b> as with {@link CacheLoader#load}, {@code loader} <b>must not</b> return
     * {@code null}; it may either return a non-null value or throw an exception.
     *
     * <p>No observable state associated with this cache is modified until loading completes.
     *
     * @since 11.0
     */
    @SuppressWarnings("unchecked")
    public static synchronized <V> V get(String key, Callable<? extends V> loader) {
        if (null == key) {
            return null;
        }
        try {
            return (V) GUAVA_CACHE.get(key.trim(), loader);
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * 根据<code>clazz.getName()</code>获取一个数据
     *
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T get(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }
        try {
            return (T) GUAVA_CACHE.getIfPresent(clazz.getName());
        } catch (Exception e) {
        }
        return null;

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
