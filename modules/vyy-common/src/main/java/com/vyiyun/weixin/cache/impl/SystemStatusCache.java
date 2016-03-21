/**
 * 
 */
package com.vyiyun.weixin.cache.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.MemcacheUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 系统状态
 * 
 * @author tf
 * 
 * @date 2015年8月20日 下午10:22:20
 * @version 1.0
 */
public class SystemStatusCache<T> implements ICache<T> {
	private static final Logger LOGGER = Logger.getLogger(SystemStatusCache.class);
	// protected Map<String, T> cache;
	/**
	 * 默认过期时间1分钟
	 */
	private static int EXPIRY_TIME = 2 * 60 * 1000;

	/** Cache with no limit */
	public SystemStatusCache() {
		// this.cache = new HashMap<String, T>();
	}

	/**
	 * 使用类型加名称
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(String id) {
		return (T) MemcacheUtil.getIns().getMemCached().get(id);
		// return (T) cache.get(id);
	}

	@Override
	public void add(String id, T object) {
		// cache.put(id, object);
		MemcacheUtil.getIns().getMemCached().set(id, object, new Date(EXPIRY_TIME));
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
		// cache.remove(id);
		MemcacheUtil.getIns().getMemCached().delete(id);
	}

	@Override
	public void clear() {
		// cache.clear();
	}

	@Override
	public void init() {
		LOGGER.info("初始化系统状态配置...");
		// 初始化系统配置
		try {
			ISystemStatusService systemStatusService = SpringContextHolder.getApplicationContext().getBean(
					ISystemStatusService.class);
			List<SystemStatus> systemStatusList = systemStatusService.getSystemStatus(null);
			if (!CollectionUtils.isEmpty(systemStatusList)) {
				ICache<Object> iCahce = SystemCacheUtil.getInstance().getSystemStatusCache();
				Map<String, List<SystemStatus>> systemStatusMap = new HashMap<String, List<SystemStatus>>();
				String key = null;
				for (SystemStatus systemStatus : systemStatusList) {
					key = systemStatus.getType();
					if (StringUtil.isNotEmpty(systemStatus.getCorpId())) {
						key = systemStatus.getCorpId() + '_' + systemStatus.getType();
					} else {
						key = systemStatus.getType();
					}
					if (!systemStatusMap.containsKey(key)) {
						systemStatusMap.put(key, new ArrayList<SystemStatus>());
						iCahce.add(key, systemStatusMap.get(key));
					}
					systemStatusMap.get(key).add(systemStatus);
				}
			}
			LOGGER.info("初始化系统状态配置完成");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("初始化系统状态配置失败!", e);
		}
	}

	/**
	 * 根据类型获取状态
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	public SystemStatus getSystemStatus(String corpId, String type, String status) {
		ISystemStatusService systemStatusService = SpringContextHolder.getApplicationContext().getBean(
				ISystemStatusService.class);
		return systemStatusService.getSystemStatus(corpId, type, status);
	}

	/**
	 * 根据类型获取状态
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	public List<SystemStatus> getSystemStatus(String corpId, String type) {
		ISystemStatusService systemStatusService = SpringContextHolder.getApplicationContext().getBean(
				ISystemStatusService.class);
		return systemStatusService.getSystemStatus(corpId, type);
	}

	/**
	 * 获取系统状态配置
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	public String getSystemStatusName(String type, String status) {
		SystemStatus systemStatus = getSystemStatus(null, type, status);
		return (systemStatus != null) ? systemStatus.getName() : "";
	}

	/**
	 * 获取业务状态配置
	 * 
	 * @param corpId
	 *            企业corpId
	 * @param type
	 * @param status
	 * @return
	 */
	public String getSystemStatusName(String corpId, String type, String status) {
		List<SystemStatus> systemStatusList = getSystemStatus(corpId, type);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			for (SystemStatus systemStatus : systemStatusList) {
				if (systemStatus.getStatus().equals(status)) {
					return systemStatus.getName();
				}
			}
		}
		return "";
	}

	/**
	 * 获取业务状态配置
	 * 
	 * @param corpId
	 *            企业corpId
	 * @param type
	 * @param status
	 * @return
	 */
	public String getSystemStatusValue(String corpId, String type, String value) {
		List<SystemStatus> systemStatusList = getSystemStatus(corpId, type);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			for (SystemStatus systemStatus : systemStatusList) {
				if (systemStatus.getValue().equals(value)) {
					return systemStatus.getName();
				}
			}
		}
		return "";
	}
}
