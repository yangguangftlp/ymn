/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.dao.SystemStatusMapper;
import com.vyiyun.weixin.entity.SystemStatus;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;

/**
 * 系统配置
 * 
 * @author tf
 * 
 *         2015年6月27日 下午7:27:55
 */
@Service
public class SystemStatusServiceImpl implements ISystemStatusService {

	@Autowired
	private SystemStatusMapper systemStatusDao;

	@Override
	public void addSystemStatus(SystemStatus systemStatus) {
		if (systemStatus.getValue() == null) {
			systemStatusDao.addSystemStatusOnNullValue(systemStatus);
		} else {
			List<SystemStatus> list = new ArrayList<SystemStatus>();
			list.add(systemStatus);
			systemStatusDao.addSystemStatus(list);
		}
	}

	@Override
	public List<SystemStatus> getSystemStatus(SystemStatus systemStatus) {
		return systemStatusDao.getSystemStatus(systemStatus);
	}

	@Override
	public DataResult getSystemStatusByPage(SystemStatus systemStatus,
			Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(systemStatus);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		List<SystemStatus> systemStatusList = systemStatusDao
				.getSystemStatusByPage(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			dataResult.setData(systemStatusList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public int deleteBySystemStatusId(String... id) {
		List<String> ids = new ArrayList<String>();
		ids.addAll(Arrays.asList(id));
		return systemStatusDao.deleteBySystemStatusId(ids);

	}

	@Override
	public void updateSystemStatus(SystemStatus systemStatus) {
		systemStatusDao.updateSystemStatus(systemStatus);

	}

	@Override
	public SystemStatus getSystemStatus(String corpId, String type, String value) {
		List<SystemStatus> systemStatusList = getSystemStatus(corpId, type);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			String svalue = null;
			for (SystemStatus systemStatus : systemStatusList) {
				svalue = systemStatus.getValue();
				if (StringUtil.isNotEmpty(svalue) && svalue.equals(value)) {
					return systemStatus;
				}
			}
		}
		return null;
	}

	@Override
	public List<SystemStatus> getSystemStatus(String corpId, String type) {
		ICache<Object> iCahce = SystemCacheUtil.getInstance()
				.getSystemStatusCache();
		String key = corpId == null ? type : corpId + "_" + type;
		@SuppressWarnings("unchecked")
		List<SystemStatus> systemStatusList = (List<SystemStatus>) iCahce
				.get(key);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			return systemStatusList;
		}
		SystemStatus systemStatus = new SystemStatus();
		systemStatus.setCorpId(corpId);
		systemStatus.setType(type);
		systemStatusList = systemStatusDao.getSystemStatus(systemStatus);
		if (!CollectionUtils.isEmpty(systemStatusList)) {
			iCahce.add(key, systemStatusList);
			return systemStatusList;
		}
		return null;
	}

	@Override
	public List<SystemStatus> getSystemStatus(String corpId, String type,
			boolean filterFlag) {
		List<SystemStatus> list = getSystemStatus(corpId, type);
		if (!CollectionUtils.isEmpty(list)) {
			if (filterFlag) {
				List<SystemStatus> retList = Lists.newArrayList();
				for (SystemStatus systemStatus : list) {
					if (!Constants.STRING_0.equals(systemStatus.getStatus())) {
						retList.add(systemStatus);
					}
				}
				if (!CollectionUtils.isEmpty(retList)) {
					return retList;
				}
			} else {
				return list;
			}
		}
		return null;
	}
}
