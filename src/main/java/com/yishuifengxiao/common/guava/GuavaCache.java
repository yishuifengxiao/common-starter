package com.yishuifengxiao.common.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

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

    private final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Cache<String, Object> GUAVA_CACHE =
            CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(24, TimeUnit.HOURS).build();


    /**
     * <p>从缓存中根据key获取一个指定数据</p>
     * <p>当缓存中不存在该key对应的值是否则调用Supplier函数获取结果，并将结果放入到缓存中，然后输出该结果</p>
     *
     * @param key      缓存的key
     * @param supplier 若缓存的key对应的数据不存在，则调用此函数获取结果并输出
     * @param <V>      数据类型
     * @return 获取的结果
     */
    public static <V> V get(String key, Supplier<V> supplier) {

        Object val = get(key);
        if (null != val) {
            return (V) val;
        }
        if (null == supplier) {
            return null;
        }
        synchronized (GuavaCache.class) {
            if (val == null) {
                val = supplier.get();
                GUAVA_CACHE.put(key, val);
            }
        }
        return (V) val;
    }

    /**
     * <p>存储一个数据</p>
     * <p>存储的时的key值默认为<code>value.getClass().getName()</code></p>
     *
     * @param value 待存储的数据
     */
    public static void put(Object value) {
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
    public static void currentPut(Object value) {
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
    public static void put(String key, Object value) {
        if (StringUtils.isBlank(key) || null == value) {
            return;
        }

        try {
            lock.writeLock().lock();
            GUAVA_CACHE.put(key.trim(), value);
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * 根据数据的key获取数据
     *
     * @param key 待存储的数据的key
     * @return 获取到的存储数据
     */
    public static Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        try {
            lock.readLock().lock();
            return GUAVA_CACHE.getIfPresent(key.trim());
        } finally {
            lock.readLock().unlock();
        }

    }

    /**
     * 获取当前线程中存储的数据
     *
     * @return 获取到的存储数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T currentGet(Class<T> clazz) {
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
    public static <T> T get(String key, Class<T> clazz) {
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
    public static <T> T get(Class<T> clazz) {
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
    public static <T> T getAndRemove(String key, Class<T> clazz) {
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
    public static <T> T getAndRemove(Class<T> clazz) {
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
    public static Object getAndRemove(String key) {

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
    public static <T> T currentAndRemove(Class<T> clazz) {

        return getAndRemove(Thread.currentThread().getId() + clazz.getName(), clazz);
    }

    /**
     * 移除存储的数据
     *
     * @param key 待移除的数据的key
     */
    public static void remove(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        try {
            lock.writeLock().lock();
            GUAVA_CACHE.invalidate(key.trim());
        } finally {
            lock.writeLock().unlock();
        }

    }

    /**
     * <p>移除存储的数据</p>
     * <p>此方式下默认key为<code>clazz.getName()</code></p>
     *
     * @param <T>   数据的类型
     * @param clazz 待移除的数据的key
     */
    public static <T> void remove(Class<T> clazz) {
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
    public static Set<String> keys() {
        try {
            lock.readLock().lock();
            return GUAVA_CACHE.asMap().keySet();
        } finally {
            lock.readLock().unlock();
        }

    }

    /**
     * 所有存储的数据的key中是否包含指定的key
     *
     * @param key 指定的key
     * @return 包含返回为true, 否则为false
     */
    public static boolean containsKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        try {
            lock.readLock().lock();
            return GUAVA_CACHE.asMap().containsKey(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 清空所有存储的数据
     */
    public static void clear() {

        try {
            lock.writeLock().lock();
            GUAVA_CACHE.cleanUp();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
