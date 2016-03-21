/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.dao.AccessoryMapper;
import com.vyiyun.weixin.dao.EntityAccessoryMapper;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.entity.EntityAccessory;
import com.vyiyun.weixin.exception.VyiyunBusinessException;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.IOUtil;
import com.vyiyun.weixin.utils.ImageUtil;
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * @author tf
 * 
 *         2015年6月24日
 */
@Service("accessoryService")
public class AccessoryServiceImpl implements IAccessoryService {

	private static final Logger LOGGER = Logger.getLogger(AccessoryServiceImpl.class);

	@Autowired
	private AccessoryMapper accessoryDao;

	@Autowired
	private EntityAccessoryMapper entityAccessoryDao;

	@Override
	public int addAccessory(Accessory... accessory) {
		List<Accessory> accessorys = new ArrayList<Accessory>();
		accessorys.addAll(Arrays.asList(accessory));
		return accessoryDao.addAccessory(accessorys);
	}

	@Override
	public void deleteAccessoryById(String id) {
		accessoryDao.deleteAccessoryById(id);
		EntityAccessory entityAccessory = new EntityAccessory();
		entityAccessory.setFileId(id);
		entityAccessoryDao.deleteEntityAccessory(entityAccessory);
	}

	@Override
	public List<Accessory> getAccessory(Accessory accessory) {
		return accessoryDao.getAccessory(accessory);
	}

	@Override
	public void updateAccessory(Accessory accessory) {
		accessoryDao.updateAccessory(accessory);
	}

	public void deleteAccessory(String entityId, String fileId) {
		EntityAccessory entityAccessory = new EntityAccessory();
		entityAccessory.setEntityId(entityId);
		entityAccessory.setFileId(fileId);
		entityAccessoryDao.deleteEntityAccessory(entityAccessory);
	}

	@Override
	public void addAccessory(String entityId, List<FileItem> fileltems) {
		if (!CollectionUtils.isEmpty(fileltems)) {
			List<Accessory> fileList = new ArrayList<Accessory>();
			List<EntityAccessory> entityAccessoryList = new ArrayList<EntityAccessory>();
			String storagePath = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ACCESSORY_STORAGE_PATH);
			String fileDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.FILE_DIR, "resource");
			String zoomDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ZOOM_DIR, "imageZoom");
			if (StringUtils.isEmpty(storagePath)) {
				LOGGER.error("附件存储路径不存在!");
				throw new VyiyunException("附件存储路径不存在!");
			}
			File storageDir = new File(storagePath);
			File fileDir = new File(storageDir + File.separator + fileDirName);
			File zoomDir = new File(storageDir + File.separator + zoomDirName);
			// 创建存储目录
			if (!storageDir.exists()) {
				storageDir.mkdirs();
			} else if (!storageDir.isDirectory()) {
				LOGGER.error("附件存储路径不是目录!");
				throw new VyiyunException("附件存储路径不是目录!");
			}

			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			if (!zoomDir.exists()) {
				zoomDir.mkdirs();
			}

			long millis = System.currentTimeMillis();
			Accessory accessory = null;
			EntityAccessory entityAccessory = null;
			for (FileItem fileItem : fileltems) {
				if (!fileItem.isFormField()) {
					accessory = new Accessory();
					accessory.setId(CommonUtil.GeneGUID());
					accessory.setFileName(millis + '_' + fileItem.getName());
					accessory.setFilePath(fileDir.getPath());
					accessory.setZoomPath(zoomDir.getPath());
					fileList.add(accessory);

					entityAccessory = new EntityAccessory();
					entityAccessory.setEntityId(entityId);
					entityAccessory.setFileId(accessory.getId());
					entityAccessoryList.add(entityAccessory);
				}
			}
			// 记录数据库
			addAccessory(fileList.toArray(new Accessory[] {}));
			entityAccessoryDao.addEntityAccessory(entityAccessoryList);

			// 存储附件
			File file = null;
			FileItem fileItem = null;
			for (int i = 0, size = fileltems.size(); i < size; i++) {
				fileItem = fileltems.get(i);
				if (!fileItem.isFormField()) {
					file = new File(fileDir, millis + '_' + fileItem.getName());
					try {
						fileItem.write(file);
						ImageUtil.getInstance().zoomImage(file, new File(zoomDir, millis + '_' + fileItem.getName()));
					} catch (Exception e) {
						LOGGER.error("附件保存出现异常,数据开始回滚 ...!", e);
						// 删除以创建的资源
						for (int j = i; j >= 0; j--) {
							fileItem = fileltems.get(j);
							file = new File(fileDir, fileItem.getName());
							file.delete();
							file = new File(zoomDir, fileItem.getName());
							if (file.exists()) {
								file.delete();
							}
						}
						LOGGER.error("附件保存出现异常,数据回滚成功 ...!", e);
					}
				}
			}
		}
	}

	@Override
	public List<Accessory> getAccessoryByEntityId(String entityId) {
		return accessoryDao.getAccessoryByEntityId(entityId);
	}

	@Override
	public Map<String, String> downloadAccessory(String mediaId) {
		try {
			JSONObject jsonObject = WeixinUtil.getMedia(mediaId);
			// 获取错误码 如果存在
			String errcode = jsonObject.getString("errcode");
			if ("40007".equals(errcode)) {
				throw new VyiyunException("获取附件资源失败,原因：" + jsonObject.getString("errmsg"));
			}
			// 获取文件名
			String fileName = jsonObject.getString("filename");
			// 获取附件资源
			return storageAccessory(fileName, new ByteArrayInputStream(jsonObject.getBytes("buffer")));
		} catch (Exception e) {
			e.printStackTrace();
			throw new VyiyunException("附件处理出现错误！", e);
		}
	}

	@Override
	public void addAccessory(String entityId, JSONObject jsonObject) {
		if (null != jsonObject) {
			JSONArray jsonArray = jsonObject.getJSONArray("mediaIds");
			if (!CollectionUtils.isEmpty(jsonArray)) {
				List<String> mediaIds = new ArrayList<String>(jsonArray.size());
				for (int i = 0, size = jsonArray.size(); i < size; i++) {
					mediaIds.add(jsonArray.getString(i));
				}
				List<EntityAccessory> entityAccessorys = new ArrayList<EntityAccessory>();
				for (int i = 0, size = mediaIds.size(); i < size; i++) {
					entityAccessorys.add(new EntityAccessory(entityId, mediaIds.get(i)));
				}
				entityAccessoryDao.addEntityAccessory(entityAccessorys);
			}
		}

	}

	public void addAccessorys(String entityId, JSONObject jsonObject) {
		if (null != jsonObject) {
			JSONArray jsonArray = jsonObject.getJSONArray("accessory");
			if (!CollectionUtils.isEmpty(jsonArray)) {
				JSONObject jsonObj = null;
				EntityAccessory entityAccessory = null;
				List<EntityAccessory> entityAccessoryList = new ArrayList<EntityAccessory>();
				String mediaId = null;
				String bType = null;
				for (int i = 0, size = jsonArray.size(); i < size; i++) {
					jsonObj = jsonArray.getJSONObject(i);
					mediaId = jsonObj.getString("mediaId");
					bType = jsonObj.getString("bType");
					if (StringUtils.isEmpty(mediaId)) {
						throw new VyiyunBusinessException("附件mediaId不能为空!");
					}
					entityAccessory = new EntityAccessory();
					entityAccessory.setEntityId(entityId);
					entityAccessory.setFileId(mediaId);
					entityAccessory.setbType(StringUtils.isNotEmpty(bType) ? bType : "0");
					entityAccessoryList.add(entityAccessory);
				}
				entityAccessoryDao.addEntityAccessory(entityAccessoryList);
			}
		}

	}

	@Override
	public void deleteInvalidData() {
		accessoryDao.deleteInvalidData();
	}

	@Override
	public List<Accessory> getInvalidData() {
		return accessoryDao.getInvalidData();
	}

	@Override
	public Map<String, String> uploadAccessory(MultipartFile multipartFile) {
		// 获取文件名
		String fileName = multipartFile.getOriginalFilename();
		try {
			// 获取附件资源
			int lastIndex = fileName.lastIndexOf('.');
			String newFileName = CommonUtil.GeneGUID();
			if (lastIndex > 0) {
				newFileName += fileName.substring(lastIndex);
			}
			return storageAccessory(newFileName, new ByteArrayInputStream(multipartFile.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof VyiyunException) {
				throw (VyiyunException) e;
			}
			throw new VyiyunException("附件处理出现错误！", e);
		}
	}

	private Map<String, String> storageAccessory(String fileName, InputStream inputStream) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			String storagePath = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ACCESSORY_STORAGE_PATH);
			String resourceDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.FILE_DIR, "resource");
			String zoomDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ZOOM_DIR, "imageZoom");
			if (StringUtils.isEmpty(storagePath)) {
				LOGGER.error("附件存储路径不存在!");
				throw new VyiyunException("附件存储路径不存在!");
			}
			File storageDir = new File(storagePath);
			File resourceDir = new File(storageDir + File.separator + resourceDirName);
			File zoomDir = new File(storageDir + File.separator + zoomDirName);
			// 创建存储目录
			if (!storageDir.exists()) {
				storageDir.mkdirs();
			} else if (!storageDir.isDirectory()) {
				LOGGER.error("附件存储路径不是目录!");
				throw new VyiyunException("附件存储路径不是目录!");
			}

			if (!resourceDir.exists()) {
				resourceDir.mkdirs();
			}
			if (!zoomDir.exists()) {
				zoomDir.mkdirs();
			}
			Accessory accessory = new Accessory();
			accessory.setId(CommonUtil.GeneGUID());
			accessory.setFileName(fileName);
			accessory.setFilePath(resourceDir.getPath());
			accessory.setZoomPath(zoomDir.getPath());
			// 记录数据库
			addAccessory(accessory);
			// 存储附件
			File file = new File(resourceDir, fileName);
			OutputStream outputStream = new FileOutputStream(file);
			try {
				IOUtil.copyStream(inputStream, outputStream);
				ImageUtil.getInstance().zoomImage(file, new File(zoomDir, fileName));
				resultMap.put("thumb", HttpRequestUtil.getInst().getString("resPath") + "/01/" + zoomDirName + '/'
						+ fileName);
				resultMap.put("original", HttpRequestUtil.getInst().getString("resPath") + "/01/" + resourceDirName
						+ '/' + fileName);
				resultMap.put("accessoryId", accessory.getId());
			} catch (Exception e) {
				LOGGER.error("附件处理出现错误!", e);
				// 删除以创建的资源
				file = new File(resourceDir, fileName);
				file.delete();
				file = new File(zoomDir, fileName);
				if (file.exists()) {
					file.delete();
				}
				throw new VyiyunException("附件处理出现错误,进行回滚!", e);
			} finally {
				outputStream.close();
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof VyiyunException) {
				throw (VyiyunException) e;
			}
			throw new VyiyunException("附件处理出现错误！", e);
		}
		return resultMap;
	}
}
