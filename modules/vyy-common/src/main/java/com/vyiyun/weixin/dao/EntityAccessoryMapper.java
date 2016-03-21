package com.vyiyun.weixin.dao;

import java.util.List;

import com.vyiyun.weixin.entity.EntityAccessory;

public interface EntityAccessoryMapper {

	/**
	 * 添加实体和附件的关联
	 * 
	 * @param EntityAccessory
	 */
	public void addEntityAccessory(List<EntityAccessory> entityAccessory);

	/**
	 * 获取实体对应关系
	 * 
	 * @param EntityAccessory
	 * @return
	 */
	public List<EntityAccessory> getEntityAccessory(EntityAccessory entityAccessory);

	/**
	 * 根据实体id删除记录 并且或删除与其相关联的的表数据
	 * 
	 * @param EntityAccessory
	 */
	public int deleteEntityAccessory(EntityAccessory entityAccessory);

	/**
	 * 更新实体文件关系
	 * 
	 * @param EntityAccessory
	 */
	public void updateEntityAccessory(EntityAccessory entityAccessory);

	public void deleteEntityAccessoryByEntityId(String entityId);

	/**
	 * 根据借款ID列表删除实体-借款记录
	 * 
	 * @param ids
	 *            借款ID列表
	 * @return 操作数
	 */
	public int deleteEntityAccessoryByLoanIds(List<String> ids);

}
