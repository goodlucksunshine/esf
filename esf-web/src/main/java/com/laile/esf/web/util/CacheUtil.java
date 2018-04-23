package com.laile.esf.web.util;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.pool.sizeof.SizeOf;

/**
 * Created by zkd on 2016/8/10.
 */
public class CacheUtil {

    private static CacheManager cacheManager = CacheManager.create();

    private static final String SYS_CACHE = "sysCache";

    /**
     * 获取SYS_CACHE缓存
     *
     * @param key
     *
     * @return
     */
    public static Object get(String key) {
        return get(SYS_CACHE, key);
    }

    /**
     * 写入SYS_CHACHE缓存
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        put(SYS_CACHE, key, value);
    }

    /**
     * 从SYS_CACHE将缓存移除
     *
     * @param key
     */
    public static void remove(String key) {
        remove(SYS_CACHE, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName
     * @param key
     *
     * @return
     */
    public static Object get(String cacheName, String key) {
        Cache cache = getCache(cacheName);
        //获取当前线程的Read锁
        cache.acquireReadLockOnKey(key);
        Element element = null;
        try {
            element = cache.get(key);
        } finally {
            cache.releaseReadLockOnKey(key);
        }
        return element == null ? null : element.getObjectValue();
    }

    /**
     * 写入缓存
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public static void put(String cacheName, String key, Object value) {
        Cache cache = getCache(cacheName);
        Element element = new Element(key, value);
        //获取当前线程Write锁
        cache.acquireWriteLockOnKey(key);
        try {
            cache.put(element);
        } finally {
            cache.releaseWriteLockOnKey(key);
        }
    }

    /**
     * 从缓存移除
     *
     * @param cacheName
     * @param key
     */
    public static void remove(String cacheName, String key) {
        getCache(cacheName).remove(key);
    }

    /**
     * 获得一个缓存，没有就创建已给
     *
     * @param cacheName
     *
     * @return
     */
    public static Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            cacheManager.addCache(cacheName);
            cache = cacheManager.getCache(cacheName);
            cache.getCacheConfiguration().setEternal(true);
        }
        return cache;
    }

    /**
     * 计算缓存大小
     *
     * @param sizeOf   new ReflectionSizeOf()/UnsafeSizeOf()/AgentSizeOf()
     * @param instance
     *
     * @return
     */
    public static long calculate(SizeOf sizeOf, Object instance) {
        return sizeOf.sizeOf(instance);
    }

}
