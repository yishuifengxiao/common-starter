package com.yishuifengxiao.common.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
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
            return (V) GUAVA_CACHE.get(key.trim(), null != loader ? loader : () -> null);
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * <p>存储一个数据</p>
     * <p>存储的时的key值默认为<code>value.getClass().getName()</code></p>
     *
     * @param value 待存储的数据
     */
    public static synchronized void put(Object value) {
        if (null == value) {
            return;
        }
        put(value.getClass().getName(), value);
    }

    /**
     * <p>存储一个数据到当前线程中</p>
     * <p>存储的时的key值默认为<code>Thread.currentThread().getId()</code></p>
     *
     * @param value 待存储的数据
     */
    public static synchronized void currentPut(Object value) {
        if (null == value) {
            return;
        }
        put(Thread.currentThread().getId() + value.getClass().getName(), value);
    }

    /**
     * <p>存储一个数据</p>
     *
     * @param key   待存储的数据的key
     * @param value 待存储的数据
     */
    public static synchronized void put(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            return;
        }
        GUAVA_CACHE.put(key.trim(), value);
    }

    /**
     * 根据数据的key获取数据
     *
     * @param key 待存储的数据的key
     * @return 获取到的存储数据
     */
    public static synchronized Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return GUAVA_CACHE.getIfPresent(key.trim());
    }

    /**
     * 获取当前线程中存储的数据
     *
     * @return 获取到的存储数据
     */
    public static synchronized <T> T currentGet(Class<T> clazz) {
        return (T) get(Thread.currentThread().getId() + clazz.getName());
    }


    /**
     * 根据数据的key获取数据
     *
     * @param key   待存储的数据的key
     * @param clazz 数据的类型Class
     * @param <T>   数据的类型
     * @return 获取到的存储数据
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T get(String key, Class<T> clazz) {
        try {
            return (T) get(key.trim());
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * <p>根据数据的key获取数据</p>
     * <p>此方式下默认key为<code>clazz.getName()</code></p>
     *
     * @param clazz 数据的类型Class
     * @param <T>   数据的类型
     * @return 获取到的存储数据
     */
    public static synchronized <T> T get(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }
        return get(clazz.getName(), clazz);
    }

    /**
     * 根据数据的key获取数据，若成功获取到此数据则从缓存中删除此数据
     *
     * @param key   待存储的数据的key
     * @param clazz 数据的类型Class
     * @param <T>   数据的类型
     * @return 获取到的存储数据
     */
    public static synchronized <T> T getAndRemove(String key, Class<T> clazz) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        try {
            T value = get(key.trim(), clazz);
            if (null != value) {
                remove(key.trim());
            }
            return value;
        } catch (Exception e) {
        }
        return null;

    }

    /**
     * <p>根据数据的key获取数据，若成功获取到此数据则从缓存中删除此数据</p>
     * <p>此方式下默认key为<code>clazz.getName()</code></p>
     *
     * @param clazz 数据的类型Class
     * @param <T>   数据的类型
     * @return 获取到的存储数据
     */
    public static synchronized <T> T getAndRemove(Class<T> clazz) {
        if (null == clazz) {
            return null;
        }

        return getAndRemove(clazz.getName(), clazz);
    }

    /**
     * 根据数据的key获取数据，若成功获取到此数据则从缓存中删除此数据
     *
     * @param key 待存储的数据的key
     * @return 获取到的存储数据
     */
    public static synchronized Object getAndRemove(String key) {

        Object value = get(key);
        if (null != value) {
            remove(key);
        }
        return value;
    }

    /**
     * 获取当前线程中存储的数据,若存在则删除此数据
     *
     * @return 获取到的存储数据
     */
    public static synchronized <T> T currentAndRemove(Class<T> clazz) {

        return getAndRemove(Thread.currentThread().getId() + clazz.getName(), clazz);
    }

    /**
     * 移除存储的数据
     *
     * @param key 待移除的数据的key
     */
    public static synchronized void remove(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        GUAVA_CACHE.invalidate(key.trim());
    }

    /**
     * <p>移除存储的数据</p>
     * <p>此方式下默认key为<code>clazz.getName()</code></p>
     *
     * @param <T>   数据的类型
     * @param clazz 待移除的数据的key
     */
    public static synchronized <T> void remove(Class<T> clazz) {
        if (null == clazz) {
            return;
        }
        remove(clazz.getName());
    }

    /**
     * 获取所有存储的数据的key
     *
     * @return 所有存储的数据的key
     */
    public static synchronized Set<String> keys() {
        return GUAVA_CACHE.asMap().keySet();
    }

    /**
     * 所有存储的数据的key中是否包含指定的key
     *
     * @param key 指定的key
     * @return 包含返回为true, 否则为false
     */
    public static synchronized boolean containsKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return GUAVA_CACHE.asMap().containsKey(key);
    }

    /**
     * 清空所有存储的数据
     */
    public static synchronized void clear() {
        GUAVA_CACHE.cleanUp();
    }

}
