/**
 * 
 */
package com.vyiyun.common.weixin.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vyiyun.common.weixin.dao.EntityProgressMapper;
import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.common.weixin.service.IEntityProgressService;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("entityProgressService")
public class EntityProgressServiceImpl implements IEntityProgressService {

	@Autowired
	private EntityProgressMapper entityProgressDao;

	@Override
	public int addEntityProgress(EntityProgress... entityProgress) {
		if (entityProgress.length < 1) {
			return 0;
		}
		return entityProgressDao.addEntityProgress(Arrays.asList(entityProgress));

	}

	@Override
	public void deleteEntityProgress(EntityProgress entityProgress) {
		entityProgressDao.deleteEntityProgress(entityProgress);

	}

	@Override
	public void updateEntityProgress(EntityProgress entityProgress) {
		entityProgressDao.updateEntityProgress(entityProgress);

	}

	@Override
	public List<EntityProgress> getEntityProgress(EntityProgress entityProgress) {
		SqlQueryParameter sqlQueryParameter = new SqlQueryParameter();
		sqlQueryParameter.setParameter(entityProgress);
		return entityProgressDao.queryEntityProgressByPage(sqlQueryParameter);
	}

	@Override
	public void deleteByEntityId(String entityId) {
		EntityProgress entityProgress = new EntityProgress();
		entityProgress.setEntityId(entityId);
		entityProgressDao.deleteEntityProgress(entityProgress);
	}

}
