package com.vyiyun.common.weixin.dao;

import java.util.List;

import com.vyiyun.common.weixin.entity.EntityProgress;
import com.vyiyun.weixin.model.SqlQueryParameter;

/**
 * 实体进展Dao
 * 
 * @author tf
 * 
 * @date 2015年7月16日 上午11:11:56
 * @version 1.0
 */
public interface EntityProgressMapper {

	/**
	 * 添加实体进展信息
	 * 
	 * @param entityProgress
	 */
	public int addEntityProgress(List<EntityProgress> entityProgressList);

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
	 * 查询记录数
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public long queryEntityProgressCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param sqlQueryParameter
	 * @return
	 */
	public List<EntityProgress> queryEntityProgressByPage(SqlQueryParameter sqlQueryParameter);

}
