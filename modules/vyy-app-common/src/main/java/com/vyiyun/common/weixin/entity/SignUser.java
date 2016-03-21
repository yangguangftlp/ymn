package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * @ClassName: SignUser
 * @Description: 用户签到类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class SignUser implements Serializable, PersistentObject {

	private static final long serialVersionUID = 3426359920089143003L;

	/*
	 * CorpId
	 */
	private String corpId;
	/*
	 * ID
	 */
	private String id;
	/*
	 * 签到信息Id
	 */
	private String signId;
	/*
	 * 用户Id
	 */
	private String userId;

	/**
	 * 用户名称
	 */
	private String userName;
	/*
	 * 签到人所在地点
	 */
	private String location;
	/*
	 * 签到时间
	 */
	private Date signTime;
	/*
	 * 出勤类型，QD为签到，QT为签退
	 */
	private String attendType;

	/**
	 * 备注
	 */
	private String remark;

	public SignUser() {
		super();
	}

	public SignUser(String signId, String userId, String location, Date signTime, String attendType, String remark,
			String corpId) {
		super();
		this.signId = signId;
		this.userId = userId;
		this.location = location;
		this.signTime = signTime;
		this.attendType = attendType;
		this.corpId = corpId;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getSignTime() {
		return signTime;
	}

	public void setSignTime(Date signTime) {
		this.signTime = signTime;
	}

	public String getAttendType() {
		return attendType;
	}

	public void setAttendType(String attendType) {
		this.attendType = attendType;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("corpId", corpId);
		persistentState.put("id", id);
		persistentState.put("signId", signId);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		persistentState.put("location", location);
		persistentState.put("signTime", signTime);
		persistentState.put("attendType", attendType);
		persistentState.put("remark", remark);
		return persistentState;
	}

}
