/**
 * 
 */
package com.vyiyun.weixin.cache.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.service.ISystemConfigService;
import com.vyiyun.weixin.utils.MemcacheUtil;
import com.vyiyun.weixin.utils.SpringContextHolder;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * @author tf
 * 
 * @date 2015年7月7日 下午2:22:14
 * @version 1.0
 * @param <T>
 */
public class SystemConfigCache<T> implements ICache<T> {
	private static final Logger LOGGER = Logger.getLogger(SystemConfigCache.class);

	/**
	 * 默认过期时间1分钟
	 */
	private static int EXPIRY_TIME = 2 * 60 * 1000;

	/** Cache with no limit */
	public SystemConfigCache() {
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
		MemcacheUtil.getIns().getMemCached().delete(id);
	}

	@Override
	public void clear() {
	}

	/**
	 * 初始化配置
	 */
	@Override
	public void init() {
		LOGGER.info("初始化系统配置...");
		try {
			// 初始化系统配置
			ISystemConfigService systemConfigService = SpringContextHolder.getApplicationContext().getBean(
					ISystemConfigService.class);
			List<SystemConfig> systemConfigList = systemConfigService.getSystemConfig(null);
			if (!CollectionUtils.isEmpty(systemConfigList)) {
				ICache<Object> iCahce = SystemCacheUtil.getInstance().getSystemConfigCache();
				String key = null;
				for (SystemConfig systemConfig : systemConfigList) {
					if (!StringUtils.isEmpty(systemConfig.getCorpId())) {
						key = systemConfig.getCorpId() + '_' + systemConfig.getType() + '_' + systemConfig.getName();
					} else {
						key = systemConfig.getType() + '_' + systemConfig.getName();
					}
					iCahce.add(key, systemConfig);
				}
			}
			LOGGER.info("初始化系统配置完成");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("初始化系统配置失败！", e);
		}
	}

	/**
	 * 获取系统配置
	 * 
	 * @param corpId
	 * @param Name
	 * @return
	 */
	public SystemConfig getSystemConfig(String corpId, String type, String name) {
		ISystemConfigService systemConfigService = SpringContextHolder.getApplicationContext().getBean(
				ISystemConfigService.class);
		return systemConfigService.getSystemConfig(corpId, type, name);
	}

	/**
	 * 获取系统配置
	 * 
	 * @param corpId
	 * @param Name
	 * @return
	 */
	public SystemConfig getSystemConfig(String type, String name) {
		ISystemConfigService systemConfigService = SpringContextHolder.getApplicationContext().getBean(
				ISystemConfigService.class);
		return systemConfigService.getSystemConfig(null, type, name);
	}
}
