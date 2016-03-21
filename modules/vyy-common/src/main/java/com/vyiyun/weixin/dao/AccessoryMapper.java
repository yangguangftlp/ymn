package com.vyiyun.weixin.dao;

import java.util.List;

import com.vyiyun.weixin.entity.Accessory;

public interface AccessoryMapper {

	/**
	 * 新增
	 * 
	 * @param accessory
	 */
	public int addAccessory(List<Accessory> accessorys);

	/**
	 * 根据条件查询获取附件信息
	 * 
	 * @param accessory
	 * @return
	 */
	public List<Accessory> getAccessory(Accessory accessory);

	/**
	 * 根据id删除记录
	 * 
	 * @param id
	 */
	public void deleteAccessoryById(String id);

	/**
	 * 更新附件
	 * 
	 * @param accessory
	 */
	public void updateAccessory(Accessory accessory);

	/**
	 * 删除无效数据
	 */
	public void deleteInvalidData();

	/**
	 * 获取无效数据
	 * 
	 * @return
	 */
	public List<Accessory> getInvalidData();

	/**
	 * 查询附件
	 * @param entityId
	 * @return
	 */
	public List<Accessory> getAccessoryByEntityId(String entityId);

	/**
	 * 根据借款ID列表删除借款附件
	 * @param ids 借款ID列表
	 * @return 操作数
	 */
	public int deleteAccessoryByLoanIds(List<String> ids);

}
