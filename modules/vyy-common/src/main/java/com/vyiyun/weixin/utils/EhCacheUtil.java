package com.vyiyun.weixin.utils;

import net.sf.ehcache.Cache;

import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.vyiyun.weixin.utils.MemcacheUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;

/**
 * ehCache 缓存工具类
 * 
 * @author tf
 * 
 * @date 上午10:25:51
 */
public class EhCacheUtil {
	private EhCacheCacheManager ehCacheCacheManager;;
	private static EhCacheUtil instance;

	private EhCacheUtil() {
		this.ehCacheCacheManager = SpringContextHolder.getBean("cacheManagerFactory");
	}

	public static EhCacheUtil getInstance() {
		if (null == instance) {
			synchronized (MemcacheUtil.class) {
				if (null == instance) {
					instance = new EhCacheUtil();
				}
			}
		}
		return instance;
	}

	public Cache getCache(String name) {
		return (Cache) this.ehCacheCacheManager.getCache(name);
	}
}
