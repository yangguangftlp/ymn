package com.vyiyun.weixin.dao;

import java.util.List;
import java.util.Map;

import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.model.SqlQueryParameter;

public interface EntityAccountMapper {

	/**
	 * 新增记录
	 * 
	 * @param entityAccount
	 */
	public int addEntityAccount(List<EntityAccount> entityAccount);

	/**
	 * 查询实体信息
	 * 
	 * @param entityAccount
	 * @return
	 */
	public List<EntityAccount> getEntityAccount(EntityAccount entityAccount);

	/**
	 * 根据id获取实体关系
	 * 
	 * @param id
	 * @return
	 */
	public EntityAccount getEntityAccountById(String id);

	/**
	 * 根据实体id删除
	 * 
	 * @param entityId
	 */
	public int deleteEntityAccount(EntityAccount entityAccount);

	/**
	 * 更新记录
	 * 
	 * @param entityAccount
	 */
	public int updateEntityAccount(EntityAccount entityAccount);

	/**
	 * 查询记录数
	 * 
	 * @param pageQueryParameter
	 * @return
	 */
	public long getEntityAccountCount(SqlQueryParameter sqlQueryParameter);

	/**
	 * 分页查询
	 * 
	 * @param pageQueryParameter
	 * @return
	 */
	public List<EntityAccount> getEntityAccountByPage(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取处理过的报销
	 * 
	 * @return
	 */
	public List<Map<String, Object>> queryEntityAccountExpense(SqlQueryParameter sqlQueryParameter);

	/**
	 * 获取当前业务当前审核人
	 * 
	 * @param entityAccount
	 *            查询参数（entityId, entityType）
	 * @return
	 */
	public EntityAccount getCurrentBusinessAuditAccount(EntityAccount entityAccount);

	/**
	 * 根据实体ID列表删除记录
	 * @param entityIds 实体ID列表
	 * @return 操作数
	 */
	public int deleteEntityAccountByEntityIds(List<String> entityIds);
}
