/**
 * 
 */
package com.vyiyun.weixin.cache;


/**
 * 缓存接口
 * 
 * @author tf
 * 
 *         2015年6月26日
 */
public interface ICache<T> {

	/**
	 * 初始化操作
	 */
	void init();

	T get(String id);

	void add(String id, T object);

	/**
	 * 缓存
	 * 
	 * @param id
	 * @param object
	 * @param expire
	 *            毫秒数据
	 */
	public void add(String id, T object, int expire);

	void remove(String id);

	void clear();
}
