/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.vyiyun.weixin.dao.EntityAccountMapper;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.model.DataResult;
import com.vyiyun.weixin.model.SqlQueryParameter;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("entityAccountService")
public class EntityAccountServiceImpl implements IEntityAccountService {

	@Autowired
	private EntityAccountMapper entityAccountDao;

	@Override
	public void deleteByEntityId(String entityId) {
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setEntityId(entityId);
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen start.
		entityAccount.setCorpId(HttpRequestUtil.getInst().getCurrentCorpId());
		// 微依云 公共应用 CorpId追加修正2016-01-04 By zb.shen end.
		entityAccountDao.deleteEntityAccount(entityAccount);
	}

	@Override
	public void updateEntityAccount(EntityAccount entityAccount) {
		entityAccountDao.updateEntityAccount(entityAccount);
	}

	@Override
	public DataResult queryEntityAccountRecord(EntityAccount entityAccount, Map<String, Object> params, int pageIndex,
			int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		sqlQueryParameter.setParameter(entityAccount);
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}
		dataResult.setData(entityAccountDao.getEntityAccountByPage(sqlQueryParameter));
		dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		return dataResult;
	}

	@Override
	public void addEntityAccount(EntityAccount... entityAccount) {
		List<EntityAccount> entityAccounts = new ArrayList<EntityAccount>();
		entityAccounts.addAll(Arrays.asList(entityAccount));
		entityAccountDao.addEntityAccount(entityAccounts);
	}

	@Override
	public List<EntityAccount> getEntityAccount(EntityAccount entityAccount) {
		return entityAccountDao.getEntityAccount(entityAccount);
	}

	@Override
	public void deleteByEntity(EntityAccount entityAccount) {
		entityAccountDao.deleteEntityAccount(entityAccount);
	}

	@Override
	public EntityAccount getEntityAccountById(String id) {
		EntityAccount entityAccount = entityAccountDao.getEntityAccountById(id);
		return entityAccount;
	}

	@Override
	public DataResult queryEntityAccountExpense(Map<String, Object> params, int pageIndex, int pageSize) {
		DataResult dataResult = new DataResult();
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		if (pageIndex != -1) {
			sqlQueryParameter.setPageIndex(pageIndex);
			sqlQueryParameter.setPage(true);
		}
		if (pageSize != -1) {
			sqlQueryParameter.setPageSize(pageSize);
		}
		if (!CollectionUtils.isEmpty(params)) {
			sqlQueryParameter.getKeyValMap().putAll(params);
		}

		List<Map<String, Object>> dataList = entityAccountDao.queryEntityAccountExpense(sqlQueryParameter);
		String status = null;
		if (!CollectionUtils.isEmpty(dataList)) {
			for (Map<String, Object> map : dataList) {
				status = StringUtil.getString(map.get("status"));
				if ("1".equals(status)) {
					map.put("statusDisplay", "同意");
				} else if ("2".equals(status)) {
					map.put("statusDisplay", "退回");
				}
				map.put("screateTime", DateUtil.dateToString((Date) map.get("createTime"), "MM月dd日 HH:mm"));

			}
			dataResult.setData(dataList);
			dataResult.setTotal(sqlQueryParameter.getTotalRecord());
		}
		return dataResult;
	}

	@Override
	public void deleteById(String id) {
		EntityAccount entityAccount = new EntityAccount();
		entityAccount.setId(id);
		entityAccountDao.deleteEntityAccount(entityAccount);
	}

	@Override
	public long getEntityAccountCount(EntityAccount entityAccount) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(entityAccount);
		return entityAccountDao.getEntityAccountCount(sqlQueryParameter);
	}
}
