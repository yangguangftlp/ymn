/**
 * 
 */
package com.vyiyun.common.weixin.controller.mobile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.ICache;
import com.vyiyun.weixin.cache.impl.WeixinContactCache;
import com.vyiyun.weixin.constant.Constants;
import com.vyiyun.weixin.entity.Accessory;
import com.vyiyun.weixin.exception.VyiyunException;
import com.vyiyun.weixin.model.WeixinDept;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IAccessoryService;
import com.vyiyun.weixin.service.IEntityAccessoryService;
import com.vyiyun.weixin.service.IEntityAccountService;
import com.vyiyun.weixin.service.ISystemStatusService;
import com.vyiyun.weixin.utils.CommonUtil;
import com.vyiyun.weixin.utils.ConfigUtil;
import com.vyiyun.weixin.utils.DateUtil;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.StringUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinUtil;
import com.vyiyun.weixin.web.model.ResponseResult;

/**
 * 微信通讯录
 * 
 * @author tf
 * 
 * @date 下午2:37:48
 */
@Controller
@RequestMapping(value = "/mobile/common")
public class CommonController {

	private static final Logger LOGGER = Logger.getLogger(CommonController.class);

	@Autowired
	private ISystemStatusService systemStatusService;
	@Autowired
	private IEntityAccessoryService entityFileService;
	@Autowired
	private IEntityAccountService entityAccountService;
	@Autowired
	private IAccessoryService accessoryService;

	/**
	 * 删除实体如关系人
	 */
	@RequestMapping(value = "deleteEntityAccount", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object deleteEntityAccount(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String id = HttpRequestUtil.getInst().getString("id");
		if (StringUtil.isEmpty(id)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("附件id不能为空！");
			return responseResult;
		}
		try {
			entityAccountService.deleteById(id);
		} catch (Exception e) {
			LOGGER.error("解除实体关系失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 删除附件
	 */
	@RequestMapping(value = "deleteAccessory", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object deleteAccessory(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		String accessoryId = HttpRequestUtil.getInst().getString("accessoryId");
		if (StringUtils.isEmpty(accessoryId)) {
			responseResult.setStatus(-1);
			responseResult.setErrorMsg("附件id不能为空！");
			return responseResult;
		}
		try {
			// 获取实体
			Accessory accessory = new Accessory();
			accessory.setId(accessoryId);
			List<Accessory> accessoryList = accessoryService.getAccessory(accessory);
			// 删除磁盘上附件
			accessoryService.deleteAccessoryById(accessoryId);
			if (!CollectionUtils.isEmpty(accessoryList)) {
				String fileName = accessoryList.get(0).getFileName();
				String storagePath = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ACCESSORY_STORAGE_PATH);
				String resourceDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.FILE_DIR, "resource");
				String zoomDirName = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, Constants.ZOOM_DIR, "imageZoom");
				File accessoryFile = new File(storagePath + File.separator + resourceDirName, fileName);
				if (accessoryFile.exists()) {
					accessoryFile.delete();
				}
				accessoryFile = new File(storagePath + File.separator + zoomDirName, fileName);
				if (accessoryFile.exists()) {
					accessoryFile.delete();
				}
			}
		} catch (Exception e) {
			LOGGER.error("删除附件失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取所有部门列表
	 */
	@RequestMapping(value = "getDepts", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getDepts(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			WeixinContactCache<Object> weixinContactCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
					.getWeixinContactCache();
			List<WeixinDept> depts = weixinContactCache.initWeixinDept();
			if (!CollectionUtils.isEmpty(depts)) {
				responseResult.setValue(depts);
			}
		} catch (Exception e) {
			LOGGER.error("获取所有部门失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取微信标签
	 */
	@RequestMapping(value = "getWTag", method = { RequestMethod.GET, RequestMethod.POST })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public @ResponseBody
	Object getWLabel(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			WeixinContactCache iCache = (WeixinContactCache) SystemCacheUtil.getInstance().getWeixinContactCache();
			List<Map<String, Object>> allWTag = (List<Map<String, Object>>) iCache.get("AllWtag");
			if (null == allWTag) {
				// iCache.initWTag();
				// allWTag = (List<Map<String, Object>>) iCache.get("AllWtag");
			}
			if (!CollectionUtils.isEmpty(allWTag)) {
				// responseResult.setValue(allWTag);
			} else {
				// responseResult.setValue(allWTag);
			}
		} catch (Exception e) {
			LOGGER.error("获取所有标签失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取微信标签
	 */
	@RequestMapping(value = "getWTagChild", method = { RequestMethod.GET, RequestMethod.POST })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public @ResponseBody
	Object getWTagChild(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String tagId = HttpRequestUtil.getInst().getString("tagId");
			List<WeixinUser> wtagChildUserList = null;
			WeixinContactCache iCache = (WeixinContactCache) SystemCacheUtil.getInstance().getWeixinContactCache();
			responseResult.setValue(wtagChildUserList);
			wtagChildUserList = (List<WeixinUser>) iCache.get(tagId);
			if (!CollectionUtils.isEmpty(wtagChildUserList)) {
				responseResult.setValue(wtagChildUserList);
				return responseResult;
			}
			wtagChildUserList = new ArrayList<WeixinUser>();
			if (StringUtils.isNotEmpty(tagId)) {
				JSONObject jsonObject = WeixinUtil.getWTagChild(tagId);
				if (null != jsonObject) {
					String errmsg = jsonObject.getString("errmsg");
					String errcode = jsonObject.getString("errcode");
					if ("0".equals(errcode)) {
						// 人员列表
						JSONArray jsonArray = jsonObject.getJSONArray("userlist");
						if (!CollectionUtils.isEmpty(jsonArray)) {
							JSONObject jsonUserInfo = null;
							for (int i = 0, size = jsonArray.size(); i < size; i++) {
								jsonUserInfo = jsonArray.getJSONObject(i);
								if (null != jsonUserInfo && StringUtils.isNotEmpty(jsonUserInfo.getString("userid"))) {
									WeixinUser weixinUser = iCache.getUserById(jsonUserInfo.getString("userid"));
									if (null != weixinUser) {
										wtagChildUserList.add(weixinUser);
									}
								}
							}
							LOGGER.info("获取标签成员用户信息成功!" + JSON.toJSONString(jsonArray));
						}
						// 部门列表
						jsonArray = jsonObject.getJSONArray("partylist");
						List<String> departIds = new ArrayList<String>();

						if (!CollectionUtils.isEmpty(jsonArray)) {
							WeixinDept WeixinDept = null;
							for (int i = 0, size = jsonArray.size(); i < size; i++) {
								WeixinDept = iCache.getDeptById(jsonArray.getString(i));
								if (null != WeixinDept) {
									departIds.addAll(iCache.getDeptIds(jsonArray.getString(i)));
								}
							}
							LOGGER.info("获取标签成员部门信息成功!" + JSON.toJSONString(jsonArray));
							List<WeixinUser> weixinUserList = iCache.getWeixinUsers();
							if (!CollectionUtils.isEmpty(weixinUserList)) {
								for (WeixinUser weixinUser : weixinUserList) {
									for (String id : departIds) {
										if (weixinUser.isContainDept(id)) {
											if (!wtagChildUserList.contains(weixinUser)) {
												wtagChildUserList.add(weixinUser);
											}
										}
									}
								}
							}
						}
						if (!CollectionUtils.isEmpty(wtagChildUserList)) {
							iCache.add(tagId, wtagChildUserList);
						}

					} else {
						LOGGER.error("获取标签成员信息失败,原因：" + errmsg);
					}
				}
			}
			responseResult.setValue(wtagChildUserList);
		} catch (Exception e) {
			LOGGER.error("获取标签成员失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取所有用户列表
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "getUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getUser(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String deptId = HttpRequestUtil.getInst().getString("deptId");
			ICache<Object> defaultCache = SystemCacheUtil.getInstance().getDefaultCache();
			List<WeixinUser> weixinUserList = (List<WeixinUser>) defaultCache.get("AllUser");
			Map<String, List<WeixinUser>> resultMap = new HashMap<String, List<WeixinUser>>();
			resultMap.put("userlist", new ArrayList<WeixinUser>());
			if (CollectionUtils.isEmpty(weixinUserList)) {
				defaultCache.init();
				weixinUserList = (List<WeixinUser>) defaultCache.get("AllUser");
			}
			for (WeixinUser weixinUser : weixinUserList) {
				if (weixinUser.isContainDept(deptId)) {
					resultMap.get("userlist").add(weixinUser);
				}
			}
			responseResult.setValue(resultMap);
		} catch (Exception e) {
			LOGGER.error("获取所有用户失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取部门以及子部门下所有用户
	 */
	@RequestMapping(value = "getDeptUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getDeptUser(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String deptId = HttpRequestUtil.getInst().getString("deptId");
			WeixinContactCache<Object> iCache = (WeixinContactCache<Object>) SystemCacheUtil.getInstance()
					.getWeixinContactCache();
			List<WeixinUser> weixinUserList = (List<WeixinUser>) iCache.initWeixinUser();
			List<WeixinUser> userList = new ArrayList<WeixinUser>();
			List<String> deptIds = iCache.getDeptIds(deptId);
			List<String> excludeDeptIds = new ArrayList<String>();
			for (WeixinUser weixinUser : weixinUserList) {
				for (String id : deptIds) {
					if (!excludeDeptIds.contains(id)) {
						if (weixinUser.isContainDept(id)) {
							userList.add(weixinUser);
						}
					}
				}
			}
			responseResult.setValue(userList);
		} catch (Exception e) {
			LOGGER.error("获取所有用户失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 添加人员
	 */
	@RequestMapping(value = "addUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object addUser(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			String weixinUserInfo = HttpRequestUtil.getInst().getString("weixinUserInfo");
			if (StringUtils.isNotEmpty(weixinUserInfo)) {
				JSONObject jsonObject = JSON.parseObject(weixinUserInfo);
				WeixinUser weixinUser = JSON.toJavaObject(jsonObject, WeixinUser.class);
				if (StringUtils.isEmpty(weixinUser.getUserid())) {
					weixinUser.setUserid(CommonUtil.GeneGUID());
				}
				Map<String, String> resultMap = new HashMap<String, String>();
				// 如果当前存在用户那么不用同步给微信通信录 根据手机号判断手机号码。企业内必须唯一
				ICache<Object> defaultCache = SystemCacheUtil.getInstance().getDefaultCache();
				@SuppressWarnings("unchecked")
				List<WeixinUser> weixinUserList = (List<WeixinUser>) defaultCache.get("AllUser");
				WeixinUser wUser = null;
				if (!CollectionUtils.isEmpty(weixinUserList)) {
					for (int i = 0, size = weixinUserList.size(); i < size; i++) {
						wUser = weixinUserList.get(i);
						if ((StringUtils.isNotEmpty(wUser.getMobile()) && wUser.getMobile().equals(
								weixinUser.getMobile()))
								|| (StringUtils.isNotEmpty(wUser.getEmail()) && wUser.equals(weixinUser.getEmail()))) {
							resultMap.put("uId", weixinUserList.get(i).getUserid());
							resultMap.put("uName", weixinUserList.get(i).getName());
							responseResult.setValue(resultMap);
							return responseResult;
						}

					}
				}
				// 这里给用户设置部门
				String outsideDeptId = ConfigUtil.get(Constants.VYIYUN_CONFIG_PATH, "outside_WeixinDept_id", "81");
				if (StringUtils.isEmpty(outsideDeptId)) {
					throw new VyiyunException("司外人员部门配置错误!");
				}
				weixinUser.setDepartment(new String[] { outsideDeptId });
				JSONObject returnJson = WeixinUtil.addUser(weixinUser);
				if (CollectionUtils.isEmpty(returnJson)) {
					responseResult.setStatus(-1);
					responseResult.setErrorMsg("创建用户出现未知错误,请联系系统管理员!");
				} else {
					if ("0".equals(returnJson.getString("errcode"))) {
						// 成功后需要将用户添加到缓存中

						if (!CollectionUtils.isEmpty(weixinUserList)) {
							weixinUserList.add(0, weixinUser);
						}
						resultMap.put("uId", weixinUser.getUserid());
						resultMap.put("uName", weixinUser.getName());
						responseResult.setValue(resultMap);
					} else {
						responseResult.setStatus(-1);
						LOGGER.error("添加用户失败！cause:" + returnJson);
						responseResult.setErrorMsg("添加人员失败,请联系系统管理员!");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("添加人员失败!", e);
			responseResult.setStatus(-1);
			LOGGER.error("添加用户失败！cause:" + e);
			responseResult.setErrorMsg("添加人员失败,请联系系统管理员!");
		}
		return responseResult;
	}

	/**
	 * 根据经纬度获取详细地址
	 */
	@RequestMapping(value = "getLocation", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object getLocation(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		LOGGER.debug("开始获取地理位置...");
		try {
			// 获取纬度
			String lat = StringUtil.getString(HttpRequestUtil.getInst().getString("lat"));
			// 获取经度
			String lng = StringUtil.getString(HttpRequestUtil.getInst().getString("lng"));

			JSONObject location = WeixinUtil.getLocation(lat, lng);
			if (null != location && null != location.get("result")) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				resultMap.put("result", location.getJSONObject("result"));
				resultMap.put("time", DateUtil.dateToString(new Date(), "HH:mm"));
				responseResult.setValue(resultMap);
			}
			LOGGER.debug("获取地理位置成功...");
		} catch (Exception e) {
			LOGGER.error("获取地址!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 刷新缓存
	 */
	@RequestMapping(value = "refreshCache", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object refreshCache(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			SystemCacheUtil.getInstance().reload();
		} catch (Exception e) {
			LOGGER.error("刷新缓存失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取附件信息 这里需要到微信服务器上下载并返回给前端
	 */
	@RequestMapping(value = "downloadAccessory", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	Object downloadAccessory() {
		ResponseResult responseResult = new ResponseResult();
		try {
			JSONArray jsonMediaIdArray = JSON.parseArray(HttpRequestUtil.getInst().getString("mediaId"));
			if (!CollectionUtils.isEmpty(jsonMediaIdArray)) {
				List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
				for (int i = 0, size = jsonMediaIdArray.size(); i < size; i++) {
					dataList.add(accessoryService.downloadAccessory(jsonMediaIdArray.getString(i)));
				}
				responseResult.setValue(dataList);
			}
		} catch (Exception e) {
			LOGGER.error("下载附件失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}

	/**
	 * 获取附件信息 这里需要到微信服务器上下载并返回给前端
	 */
	@RequestMapping(value = "uploadAccessory", method = { RequestMethod.POST })
	public @ResponseBody
	Object uploadAccessory(HttpServletRequest request) {
		ResponseResult responseResult = new ResponseResult();
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
				MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
				Iterator<String> iter = multiRequest.getFileNames();
				List<MultipartFile> fileList = null;
				while (iter.hasNext()) {
					fileList = multiRequest.getFiles(iter.next());
					if (!CollectionUtils.isEmpty(fileList)) {
						for (MultipartFile multipartFile : fileList) {
							dataList.add(accessoryService.uploadAccessory(multipartFile));
						}
					}
				}
				responseResult.setValue(dataList);
			}
		} catch (Exception e) {
			LOGGER.error("上传附件失败!", e);
			responseResult.setStatus(-1);
		}
		return responseResult;
	}
}
