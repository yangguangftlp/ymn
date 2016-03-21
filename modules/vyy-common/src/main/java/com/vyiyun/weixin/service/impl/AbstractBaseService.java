/**
 * 
 */
package com.vyiyun.weixin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vyiyun.weixin.entity.EntityAccount;
import com.vyiyun.weixin.utils.CommonUtil;

/**
 * 抽象服务层
 * 
 * @author tf
 * 
 * @date 2015年7月30日 上午10:52:58
 * @version 1.0
 */
public abstract class AbstractBaseService {
	/**
	 * 从json对象中获取审核人以及抄送者
	 * 
	 * @param jsonInfoObj
	 * @param entityId
	 *            关联实体id
	 * @param corpId
	 *            企业ID
	 * @return
	 */
	protected List<EntityAccount> generateEntityAccount(JSONObject jsonInfoObj, String entityId, String corpId) {
		// 获取审核人 抄送者
		EntityAccount entityAccount = null;
		List<EntityAccount> entityAccountList = new ArrayList<EntityAccount>();
		JSONObject jsonObj = null;
		JSONArray jsonArray = null;
		// 实体用户信息
		if (jsonInfoObj.containsKey("entityUserInfo")) {
			jsonArray = jsonInfoObj.getJSONArray("entityUserInfo");

		} else if (jsonInfoObj.containsKey("userInfo")) {
			jsonArray = jsonInfoObj.getJSONArray("userInfo");
		}
		if (!CollectionUtils.isEmpty(jsonArray)) {
			for (Object obj : jsonArray) {
				jsonObj = (JSONObject) obj;
				entityAccount = new EntityAccount(entityId, corpId);
				entityAccount.setId(CommonUtil.GeneGUID());
				entityAccount.setAccountId(jsonObj.getString("uId"));
				entityAccount.setAccountName(jsonObj.getString("uName"));
				entityAccount.setAccountType(jsonObj.getString("uType"));
				entityAccount.setAvatar(jsonObj.getString("uAvatar"));
				entityAccount.setPersonType(jsonObj.getString("personType"));
				entityAccount.setEntityType(jsonObj.getString("entityType"));
				entityAccount.setDealResult("0");
				entityAccountList.add(entityAccount);
			}
		}
		return entityAccountList;
	}
}
