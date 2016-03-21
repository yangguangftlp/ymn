package com.vyiyun.weixin.service;

import java.util.List;
import java.util.Map;

import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.model.DataResult;

public interface IEntityAccountService {

	/**
	 * 根据id删除实体
	 * 
	 * @param id
	 */
	public void deleteById(String id);

	/**
	 * 根据实体id删除记录
	 * 
	 * @param entityId
	 */
	public void deleteByEntityId(String entityId);

	/**
	 * 删除记录
	 * 
	 * @param entityId
	 */
	public void deleteByEntity(EntityAccount entityAccount);

	/**
	 * 更新
	 * 
	 * @param entityAccount
	 */
	public void updateEntityAccount(EntityAccount entityAccount);

	/**
	 * 查询实体
	 * 
	 * @param entityAccount
	 * @return
	 */
	public List<EntityAccount> getEntityAccount(EntityAccount entityAccount);

	/**
	 * 分页查询
	 * 
	 * @param absent
	 * @param absent
	 * @param pageIndex
	 * @param pageSize
	 */
	public DataResult queryEntityAccountRecord(EntityAccount entityAccount, Map<String, Object> params, int pageIndex,
			int pageSize);

	/**
	 * 获取审批
	 * 
	 * @param params
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public DataResult queryEntityAccountExpense(Map<String, Object> params, int pageIndex, int pageSize);

	/**
	 * 添加实体信息
	 * 
	 * @param entityAccount
	 */
	public void addEntityAccount(EntityAccount... entityAccount);

	/**
	 * 根据id获取报销审核人
	 * 
	 * @param id
	 */
	public EntityAccount getEntityAccountById(String id);
	
	/**
	 * 查询记录数
	 * 
	 * @param pageQueryParameter
	 * @return
	 */
	public long getEntityAccountCount(EntityAccount entityAccount);
}
