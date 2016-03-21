/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.LinkedBlockingQueue;

import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.DefaultCache;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.cache.impl.SystemStatusCache;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.msg.MsgExecutor;

/**
 * 系统缓存
 * 
 * @author tf
 * 
 *         2015年6月26日
 */
public class SystemCacheUtil {
	private static SystemCacheUtil instance;
	// 线程副本
	private ThreadLocal<Map<String, Object>> threadLocalVar = new ThreadLocal<Map<String, Object>>();

	// 消息队列
	private LinkedBlockingQueue<MsgExecutor> linkedQueue = new LinkedBlockingQueue<MsgExecutor>(25);
	// 缓存存储
	@SuppressWarnings("rawtypes")
	private Map<Class, ICache<Object>> cacheMap = new HashMap<Class, ICache<Object>>();
	/**
	 * 默认缓存
	 */
	private ICache<Object> defaultCache;

	/**
	 * 系统配置缓存
	 */
	private ICache<Object> systemConfigCache;

	/**
	 * 系统状态配置缓存
	 */
	private ICache<Object> systemStatusCache;

	/**
	 * 通讯录缓存
	 */
	private ICache<Object> weixinContactCache;

	@SuppressWarnings("unchecked")
	private SystemCacheUtil() {
		defaultCache = new DefaultCache<Object>();
		systemConfigCache = new SystemConfigCache<Object>();
		systemStatusCache = new SystemStatusCache<Object>();
		weixinContactCache = new WeixinContactCache<Object>();

		// 预置 缓存实体
		@SuppressWarnings("rawtypes")
		ServiceLoader<ICache> iCacheServiceLoader = ServiceLoader.load(ICache.class);
		if (null != iCacheServiceLoader) {
			for (@SuppressWarnings("rawtypes")
			ICache iCache : iCacheServiceLoader) {
				cacheMap.put(iCache.getClass(), iCache);
			}
		}
	}

	public static SystemCacheUtil getInstance() {
		if (null == instance) {
			synchronized (SystemCacheUtil.class) {
				if (null == instance) {
					instance = new SystemCacheUtil();
					instance.init();
				}
			}
		}
		return instance;
	}

	public <T>  ICache<Object> getCache(Class<T> className) {
		if (cacheMap.containsKey(className)) {
			return  cacheMap.get(className);
		}
		return null;
	}

	/**
	 * 清空缓存
	 */
	public void clean() {
		defaultCache.clear();
		systemConfigCache.clear();
		systemStatusCache.clear();
		init();
	}

	/**
	 * 初始化缓存配置
	 */
	private void init() {
		defaultCache.init();
		systemConfigCache.init();
		systemStatusCache.init();
		weixinContactCache.init();
	}

	public void reload() {
		init();
	}

	public DefaultCache<Object> getDefaultCache() {
		return (DefaultCache<Object>) defaultCache;
	}

	public WeixinContactCache<Object> getWeixinContactCache() {
		return (WeixinContactCache<Object>) weixinContactCache;
	}

	public SystemConfigCache<Object> getSystemConfigCache() {
		return (SystemConfigCache<Object>) systemConfigCache;
	}

	public SystemStatusCache<Object> getSystemStatusCache() {
		return (SystemStatusCache<Object>) systemStatusCache;
	}

	/**
	 * 保存数据前线程中
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		Map<String, Object> keyValue = threadLocalVar.get();
		if (null == keyValue) {
			keyValue = new HashMap<String, Object>();
			threadLocalVar.set(keyValue);
		}
		keyValue.put(key, value);
	}

	/**
	 * 从当前线程中获取
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		Map<String, Object> keyValue = threadLocalVar.get();
		if (null != keyValue) {
			return keyValue.get(key);
		}
		return null;
	}

	public void add(MsgExecutor msgExecutor) {
		msgExecutor.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		msgExecutor.setSuiteId(HttpRequestUtil.getInst().getCurrentSuiteId());
		msgExecutor.setSuiteSecret(HttpRequestUtil.getInst().getCurrentSuiteSecret());
		linkedQueue.add(msgExecutor);
	}

	public LinkedBlockingQueue<MsgExecutor> getLinkedQueue() {
		return linkedQueue;
	}
}
