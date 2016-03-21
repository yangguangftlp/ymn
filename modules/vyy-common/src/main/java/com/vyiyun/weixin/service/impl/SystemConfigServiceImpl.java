/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.dao.SystemConfigMapper;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.service.ISystemConfigService;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 系统配置服务
 * 
 * @author tf
 * 
 * @date 下午5:21:21
 */
@Service
public class SystemConfigServiceImpl implements ISystemConfigService {
	@Autowired
	private SystemConfigMapper systemConfigDao;

	@Override
	public void deleteById(String id) {

	}

	@Override
	public int update(SystemConfig systemConfig) {
		return  systemConfigDao.updateSystemConfig(systemConfig);
	}

	@Override
	public void addSystemConfig(SystemConfig... systemConfig) {
		systemConfigDao.addSystemConfig(Arrays.asList(systemConfig));
	}

	@Override
	public List<SystemConfig> getSystemConfig(SystemConfig systemConfig) {
		return systemConfigDao.getSystemConfig(systemConfig);
	}

	@Override
	public SystemConfig getSystemConfig(String corpId, String type, String name) {
		ICache<Object> iCahce = SystemCacheUtil.getInstance().getSystemConfigCache();
		String key = corpId + "_" + type + "_" + name;
		if (StringUtils.isEmpty(corpId)) {
			key = type + "_" + name;
		}
		SystemConfig systemConfig = (SystemConfig) iCahce.get(key);
		if (null != systemConfig) {
			return systemConfig;
		}
		systemConfig = new SystemConfig();
		systemConfig.setCorpId(corpId);
		systemConfig.setType(type);
		systemConfig.setName(name);
		List<SystemConfig> systemConfigList = systemConfigDao.getSystemConfig(systemConfig);
		if (!CollectionUtils.isEmpty(systemConfigList)) {
			iCahce.add(key, systemConfigList.get(0));
			return systemConfigList.get(0);
		}
		return null;
	}
}
