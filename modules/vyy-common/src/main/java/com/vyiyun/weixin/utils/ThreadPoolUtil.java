/**
 * 
 */
package com.vyiyun.weixin.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * @author zb.shen
 * 
 *         2015年6月26日
 */
public class ThreadPoolUtil {

	// 线程副本
	private ThreadLocal<Map<String, Object>> threadLocalVar = new ThreadLocal<Map<String, Object>>();

	/**
	 * 单例实例
	 */
	private static ThreadPoolUtil instance;

	/**
	 * newly created single-threaded Executor
	 */
	private ExecutorService singleThreadExecutor;

	/**
	 * the newly created thread pool
	 */
	private ExecutorService fixedThreadPool;

	/**
	 * the newly created thread pool
	 */
	private ExecutorService cachedThreadPool;

	private ThreadPoolUtil() {
		singleThreadExecutor = Executors.newSingleThreadExecutor();
		fixedThreadPool = Executors.newFixedThreadPool(10);
		cachedThreadPool = Executors.newCachedThreadPool();
	}

	public static ThreadPoolUtil getInstance() {
		if (null == instance) {
			synchronized (ThreadPoolUtil.class) {
				if (null == instance) {
					instance = new ThreadPoolUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * @return the singleThreadExecutor
	 */
	public ExecutorService getSingleThreadExecutor() {
		if (singleThreadExecutor == null || singleThreadExecutor.isShutdown()) {
			singleThreadExecutor = Executors.newSingleThreadExecutor();
		}
		return singleThreadExecutor;
	}

	/**
	 * @return the fixedThreadPool
	 */
	public ExecutorService getFixedThreadPool() {
		if (fixedThreadPool == null || fixedThreadPool.isShutdown()) {
			fixedThreadPool = Executors.newFixedThreadPool(15);
		}
		return fixedThreadPool;
	}

	/**
	 * @return the cachedThreadPool
	 */
	public ExecutorService getCachedThreadPool() {
		if (cachedThreadPool == null || cachedThreadPool.isShutdown()) {
			cachedThreadPool = Executors.newCachedThreadPool();
		}
		return cachedThreadPool;
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

}
