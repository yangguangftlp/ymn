package com.vyiyun.weixin.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.entity.EntityAccessory;

public interface IEntityAccessoryService {

	public void addEntityAccessory(EntityAccessory... EntityAccessory);

	/**
	 * 添加实体和文件关联
	 * 
	 * @param entityId
	 *            实体
	 * @param jsonObject
	 *            附件id 信息 {mediaIds:[xx,xx]}
	 */
	public void addEntityAccessory(String entityId, JSONObject jsonObject);

	/**
	 * 添加实体和文件关联
	 * 
	 * @param entityId
	 *            实体
	 * @param mediaIds
	 *            附件id 信息[xx,xx]
	 */
	public void addEntityAccessory(String entityId, List<String> mediaIds);

	/**
	 * 根据实体id 文件id获取实体文件关联信息
	 * 
	 * @param entityId
	 * @param fileId
	 * @return
	 */
	public List<EntityAccessory> getEntityAccessoryById(String entityId, String fileId);

	/**
	 * 根据实体id 文件id 删除关系记录
	 * 
	 * @param entityId
	 * @param fileId
	 */
	public void deleteEntityAccessory(String entityId, String fileId);

	/**
	 * 根据实体id删除记录
	 * 
	 * @param entityId
	 */
	public void deleteEntityAccessory(String entityId);

	/**
	 * 更新实体文件关联
	 * 
	 * @param EntityAccessory
	 */
	public void updateEntityAccessory(EntityAccessory EntityAccessory);

	/**
	 * 删除实体文件关联
	 * @param entityAccessory 实体文件
	 * @return 更新操作数
	 */
	public int deleteEntityAccessory(EntityAccessory entityAccessory);

}
