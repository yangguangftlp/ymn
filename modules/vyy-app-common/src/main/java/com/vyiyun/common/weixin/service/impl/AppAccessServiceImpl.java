/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.common.weixin.dao.AppAccessMapper;
import com.vyiyun.common.weixin.entity.AppAccess;
import com.vyiyun.common.weixin.service.IAppAccessService;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 应用访问服务
 * 
 * @author tf
 * @date 2015年10月9日 上午9:12:29
 */
@Service("appAccessService")
public class AppAccessServiceImpl implements IAppAccessService {

	@Autowired
	private AppAccessMapper appAccessDao;

	@Override
	public int addAppAccess(AppAccess ...appAccess) {
		if (appAccess.length < 0) {
			return 0;
		}
		return appAccessDao.addAppAccess(Arrays.asList(appAccess));
	}

	@Override
	public DataResult getAppAccessRanking(AppAccess appAccess, Map<String, Object> params, int pageIndex, int pageSize) {

		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}

		sqlQueryParameter.getKeyValMap().putAll(params);
		sqlQueryParameter.setParameter(appAccess);
		List<Map<String, Object>> dataMapList = appAccessDao.getAppAccessRanking(sqlQueryParameter);
		if (!CollectionUtils.isEmpty(dataMapList)) {
			dataResult.setData(dataMapList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public void updateAppAccessByDay(AppAccess appAccess) {
		appAccessDao.updateAppAccessByDay(appAccess);
	}

}
