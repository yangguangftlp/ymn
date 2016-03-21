/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.dao.EntityAccessoryMapper;
import com.vyiyun.weixin.entity.EntityAccessory;
import com.vyiyun.weixin.service.IEntityAccessoryService;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("entityAccessoryService")
public class EntityAccessoryServiceImpl implements IEntityAccessoryService {

	@Autowired
	public EntityAccessoryMapper entityAccessoryDao;

	@Override
	public void addEntityAccessory(EntityAccessory... entityAccessory) {
		List<EntityAccessory> entityAccessorys = new ArrayList<EntityAccessory>();
		entityAccessorys.addAll(Arrays.asList(entityAccessory));
		entityAccessoryDao.addEntityAccessory(entityAccessorys);
	}

	@Override
	public List<EntityAccessory> getEntityAccessoryById(String entityId, String fileId) {
		EntityAccessory entityFile = new EntityAccessory();
		entityFile.setEntityId(entityId);
		entityFile.setFileId(fileId);
		return entityAccessoryDao.getEntityAccessory(entityFile);
	}

	@Override
	public void deleteEntityAccessory(String entityId, String fileId) {
		EntityAccessory entityFile = new EntityAccessory();
		entityFile.setEntityId(entityId);
		entityFile.setFileId(fileId);
		entityAccessoryDao.deleteEntityAccessory(entityFile);
	}

	@Override
	public void deleteEntityAccessory(String entityId) {
		entityAccessoryDao.deleteEntityAccessoryByEntityId(entityId);
	}

	@Override
	public void updateEntityAccessory(EntityAccessory entityFile) {
		entityAccessoryDao.updateEntityAccessory(entityFile);
	}

	@Override
	public void addEntityAccessory(String entityId, JSONObject jsonObject) {
		if (null != jsonObject) {
			JSONArray jsonArray = jsonObject.getJSONArray("mediaIds");
			if (!CollectionUtils.isEmpty(jsonArray)) {
				List<String> mediaIds = new ArrayList<String>(jsonArray.size());
				for (int i = 0, size = jsonArray.size(); i < size; i++) {
					mediaIds.add(jsonArray.getString(i));
				}
				addEntityAccessory(entityId, mediaIds);
			}
		}

	}

	@Override
	public void addEntityAccessory(String entityId, List<String> mediaIds) {
		EntityAccessory[] entityFile = new EntityAccessory[mediaIds.size()];
		for (int i = 0, size = mediaIds.size(); i < size; i++) {
			entityFile[i] = new EntityAccessory(entityId, mediaIds.get(i));
		}
		addEntityAccessory(entityFile);
	}

	@Override
	public int deleteEntityAccessory(EntityAccessory entityFile) {
		return entityAccessoryDao.deleteEntityAccessory(entityFile);
	}
}
