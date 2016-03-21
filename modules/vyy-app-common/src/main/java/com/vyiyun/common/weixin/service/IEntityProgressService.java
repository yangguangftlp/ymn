package com.vyiyun.common.weixin.service;

import java.util.List;

import com.vyiyun.common.weixin.entity.EntityProgress;

/**
 * 实体-进展表
 * 
 * @author tf
 * 
 * @date 2015年7月16日 上午11:10:16
 * @version 1.0
 */
public interface IEntityProgressService {

	/**
	 * 添加实体进展信息
	 * 
	 * @param entityProgress
	 */
	public int addEntityProgress(EntityProgress... entityProgress);

	/**
	 * 删除实体进展信息
	 * 
	 * @param entityProgress
	 */
	public void deleteEntityProgress(EntityProgress entityProgress);

	/**
	 * 更新实体进展信息
	 * 
	 * @param entityProgress
	 */
	public void updateEntityProgress(EntityProgress entityProgress);

	/**
	 * 获取实体进展信息
	 * 
	 * @param entityProgress
	 */
	public List<EntityProgress> getEntityProgress(EntityProgress entityProgress);

	/**
	 * 根据实体id删除记录
	 * 
	 * @param entityId
	 */
	public void deleteByEntityId(String entityId);

}
