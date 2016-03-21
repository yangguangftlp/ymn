/**
 * 
 */
package com.vyiyun.weixin.utils;

import com.danga.MemCached.MemCachedClient;

/**
 * memcache缓存工具类
 * 
 * @author tf
 * 
 * @date 下午2:46:25
 */
public class MemcacheUtil {
	private static MemCachedClient memCachedClient;
	private static MemcacheUtil instance;

	private MemcacheUtil() {
		memCachedClient = (MemCachedClient) SpringContextHolder.getBean("memcachedClient");
	}

	public static MemcacheUtil getIns() {
		if (null == instance) {
			synchronized (MemcacheUtil.class) {
				if (null == instance) {
					instance = new MemcacheUtil();
				}
			}
		}
		return instance;
	}

	public MemCachedClient getMemCached() {
		return memCachedClient;
	}
}
