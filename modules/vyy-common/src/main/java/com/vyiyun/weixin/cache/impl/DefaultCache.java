/**
 * 
 */
package com.vyiyun.weixin.cache.impl;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.utils.MemcacheUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 默认缓存
 * 
 * @author tf
 * 
 *         2015年6月26日
 */
public class DefaultCache<T> implements ICache<T> {

	private static final Logger LOGGER = Logger.getLogger(DefaultCache.class);

	// protected Map<String, T> cache;

	/** Cache with no limit */
	public DefaultCache() {
		// this.cache = new HashMap<String, T>();
	}

	/**
	 * Cache which has a hard limit: no more elements will be cached than the
	 * limit.
	 */
	public DefaultCache(final int limit) {

	}

	/**
	 * 使用类型加名称
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(String id) {
		return (T) MemcacheUtil.getIns().getMemCached().get(id);
	}

	@Override
	public void add(String id, T object) {
		MemcacheUtil.getIns().getMemCached().set(id, object);
	}

	/**
	 * 缓存
	 * 
	 * @param id
	 * @param object
	 * @param expire
	 *            毫秒数据
	 */
	public void add(String id, T object, int expire) {
		MemcacheUtil.getIns().getMemCached().set(id, object, new Date(expire));
	}

	@Override
	public void remove(String id) {
		MemcacheUtil.getIns().getMemCached().delete(id);
	}

	@Override
	public void init() {

	}

	/**
	 * 将套件中应用id换成当前企业应用id 使用场景：
	 * 
	 * @param id
	 */
	public String convertAppId(String corpId, String suiteId, String appId) {
		// String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		// String suiteId = HttpRequestUtil.getInst().getCurrentSuiteId();
		JSONObject jsonObject = (JSONObject) get(corpId + "_suite");
		LOGGER.debug("获取当前应用ID,corpId:" + corpId + ",suiteId:" + suiteId + ",套件应用信息:" + jsonObject);
		if (!CollectionUtils.isEmpty(jsonObject) && jsonObject.containsKey(suiteId)) {
			JSONArray jsonArray = jsonObject.getJSONArray(suiteId);
			if (!CollectionUtils.isEmpty(jsonArray)) {
				for (int i = 0, length = jsonArray.size(); i < length; i++) {
					jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject.getString("appId").equals(appId)) {
						return jsonObject.getString("agentId");
					}
				}

			}
		} else {
			ICache<Object> iCahe = SystemCacheUtil.getInstance().getSystemConfigCache();
			SystemConfig systemConfig = (SystemConfig) iCahe.get("system_getAgent");
			if (null != systemConfig && StringUtil.isNotEmpty(systemConfig.getValue())) {
				String url = MessageFormat.format(systemConfig.getValue(), new Object[] { corpId });
				LOGGER.debug("获取应用getAgent地址【" + url + "】");
				jsonObject = JSONObject.parseObject(HttpsUtil.sendGet(url));
				if (null != jsonObject && "true".equalsIgnoreCase(jsonObject.getString("success"))) {
					LOGGER.debug("获取应用应用ID，成功：" + jsonObject);
					SystemCacheUtil.getInstance().getDefaultCache()
							.add(corpId + "_suite", jsonObject.getJSONObject("data"));
				} else {
					LOGGER.debug("获取应用应用ID失败，原因是：" + jsonObject);
				}

			} else {
				LOGGER.debug("获取应用应用ID失败，请检查系统配置");
			}
		}
		LOGGER.error("获取当前应用ID失败corpId:" + corpId + ",suiteId:" + suiteId);
		return appId;
	}

	/**
	 * 
	 * 将套件中应用id换成当前企业应用id 使用场景：当前线程
	 * 
	 * @param id
	 */
	public String convertAppId(String appId) {
		String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
		String suiteId = HttpRequestUtil.getInst().getCurrentSuiteId();
		JSONObject jsonObject = (JSONObject) get(corpId + "_suite");
		LOGGER.debug("获取当前应用ID,corpId:" + corpId + ",suiteId:" + suiteId + ",套件应用信息:" + jsonObject);
		if (!CollectionUtils.isEmpty(jsonObject) && jsonObject.containsKey(suiteId)) {
			JSONArray jsonArray = jsonObject.getJSONArray(suiteId);
			if (!CollectionUtils.isEmpty(jsonArray)) {
				for (int i = 0, length = jsonArray.size(); i < length; i++) {
					jsonObject = jsonArray.getJSONObject(i);
					if (jsonObject.getString("appId").equals(appId)) {
						return jsonObject.getString("agentId");
					}
				}

			}
		}
		LOGGER.error("获取当前应用ID失败corpId:" + corpId + ",suiteId:" + suiteId);
		return appId;
	}

	@Override
	public void clear() {
		// TODO 自动生成的方法存根

	}
}
