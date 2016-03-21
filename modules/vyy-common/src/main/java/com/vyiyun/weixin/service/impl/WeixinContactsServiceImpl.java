/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.cache.impl.SystemConfigCache;
import com.vyiyun.weixin.entity.SystemConfig;
import com.vyiyun.weixin.enums.WeixinDepartStatus;
import com.vyiyun.weixin.model.WeixinDept;
import com.vyiyun.weixin.model.WeixinTag;
import com.vyiyun.weixin.model.WeixinUser;
import com.vyiyun.weixin.service.IWeixinContactsService;
import com.vyiyun.weixin.utils.HttpRequestUtil;
import com.vyiyun.weixin.utils.HttpsUtil;
import com.vyiyun.weixin.utils.SystemCacheUtil;
import com.vyiyun.weixin.utils.WeixinUtil;

/**
 * 微信通信录实现
 * 
 * @author tf
 * @date 2015年12月21日 下午5:18:42
 */
@Service
public class WeixinContactsServiceImpl implements IWeixinContactsService {

	private static final Logger LOGGER = Logger.getLogger(WeixinContactsServiceImpl.class);

	@Override
	public List<WeixinUser> getWeixinUserByDeptId(String deptId) {
		return getWeixinUserByDeptId(deptId, 1, WeixinDepartStatus.获取已关注成员列表);
	}

	@Override
	public List<WeixinDept> getWeixinDeptIds() {
		List<WeixinDept> weixinDeptList = new ArrayList<WeixinDept>();
		SystemConfigCache<Object> iCahe = (SystemConfigCache<Object>) SystemCacheUtil.getInstance().getSystemConfigCache();
		SystemConfig systemConfig =  iCahe.getSystemConfig("system","getDeptInfo");
		if (null != systemConfig ) {
			String corpId = HttpRequestUtil.getInst().getCurrentCorpId();
			String suiteId = HttpRequestUtil.getInst().getCurrentSuiteId();
			String url = MessageFormat.format(systemConfig.getValue(), new Object[] { corpId,suiteId });
			JSONObject jsonObject = JSONObject.parseObject(HttpsUtil.sendGet(url));
			if (!CollectionUtils.isEmpty(jsonObject) && "true".equalsIgnoreCase(jsonObject.getString("success"))) {
				JSONArray data = jsonObject.getJSONArray("data");
				WeixinDept weixinDept = null;
				if (!CollectionUtils.isEmpty(data)) {
					for (int i = 0, size = data.size(); i < size; i++) {
						weixinDept = JSON.toJavaObject(data.getJSONObject(i), WeixinDept.class);
						if (null != weixinDept) {
							weixinDeptList.add(weixinDept);
						}
					}
				}
			}
		} else {
			LOGGER.error("系统错误，获取用户信息失败，原因：url配置错误!");
		}
		return weixinDeptList;
	}

	@Override
	public List<WeixinUser> getWeixinUserByDeptId(String deptId, int fetchChild, WeixinDepartStatus... statuses) {
		List<WeixinUser> weixinUserList = new ArrayList<WeixinUser>();
		try {
			JSONObject jsonObject = WeixinUtil.getDepartUsersDetail(deptId, fetchChild, WeixinDepartStatus.获取全部成员);
			if (null != jsonObject) {
				JSONArray jsonUserList = jsonObject.getJSONArray("userlist");
				String errmsg = jsonObject.getString("errmsg");
				String errcode = jsonObject.getString("errcode");
				if ("0".equals(errcode)) {
					if (null != jsonUserList) {
						for (int i = 0, size = jsonUserList.size(); i < size; i++) {
							weixinUserList.add(JSON.toJavaObject(jsonUserList.getJSONObject(i), WeixinUser.class));
						}
						LOGGER.info("初始化所有用户信息成功!" + JSON.toJSONString(weixinUserList));
					}
				} else {
					LOGGER.error("初始化所有用户信息失败,原因：" + errmsg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("初始化所有用户信息失败!", e);
		}
		return weixinUserList;
	}

	@Override
	public List<WeixinTag> getWeixinTag() {
		// TODO 自动生成的方法存根
		return null;
	}

}
