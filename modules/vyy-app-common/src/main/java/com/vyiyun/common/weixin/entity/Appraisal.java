/**
 * 
 */
package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * 评价实体
 * 
 * @author tf
 * @date 2015年11月16日 上午10:21:28
 */
public class Appraisal implements Serializable, PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一ID
	 */
	private String id;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 主题
	 */
	private String theme;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 用户图像
	 */
	private String avatar;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 是否综合评价
	 */
	private Boolean overallMerit;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 结束时间
	 */
	private Date endTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getOverallMerit() {
		return overallMerit;
	}

	public void setOverallMerit(Boolean overallMerit) {
		this.overallMerit = overallMerit;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		persistentState.put("theme", theme);
		persistentState.put("overallMerit", overallMerit);
		persistentState.put("createTime", createTime);
		persistentState.put("endTime", endTime);
		persistentState.put("status", status);
		return persistentState;
	}
}
