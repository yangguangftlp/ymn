package com.vyiyun.weixin.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.entity.Accessory;

public interface IAccessoryService {

	/**
	 * 新增
	 * 
	 * @param accessory
	 */
	public int addAccessory(Accessory... accessory);

	/**
	 * 根据条件查询获取附件信息
	 * 
	 * @param accessory
	 * @return
	 */
	public List<Accessory> getAccessory(Accessory accessory);

	/**
	 * 根据id删除记录 以及对应关系
	 * 
	 * @param accessoryId
	 */
	public void deleteAccessoryById(String id);

	/**
	 * 这里提供获取删除无效数据操作
	 */
	public List<Accessory> getInvalidData();

	/**
	 * 这里提供删除无效数据操作
	 */
	public void deleteInvalidData();

	/**
	 * 更新附件
	 * 
	 * @param accessory
	 */
	public void updateAccessory(Accessory accessory);

	/**
	 * 添加附件信息
	 * 
	 * @param id
	 *            附件所关联的id
	 * 
	 * @param fileltems
	 * @deprecated
	 */
	@Deprecated
	public void addAccessory(String id, List<FileItem> fileltems);

	/**
	 * 根据条件查询获取附件信息
	 * 
	 * @param entityId
	 * @return
	 */
	public List<Accessory> getAccessoryByEntityId(String entityId);

	/**
	 * 从服务器下载附件 并本地存储和数据库存储
	 * 
	 * @param mediaId
	 */
	public Map<String, String> downloadAccessory(String mediaId);

	/**
	 * 添加附件信息
	 * 
	 * @param id
	 * @param jsonObject
	 */
	public void addAccessory(String entityId, JSONObject jsonObject);

	/**
	 * 上传附件
	 * 
	 * @param file
	 * @return
	 */
	public Map<String, String> uploadAccessory(MultipartFile file);
}
