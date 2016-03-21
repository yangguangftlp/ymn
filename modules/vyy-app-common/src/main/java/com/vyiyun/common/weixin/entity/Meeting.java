package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * @ClassName: Meeting
 * @Description: 会议类
 * @author XiaoWei
 * @date Dec 22, 2015 10:30:25 AM
 */
public class Meeting implements Serializable, PersistentObject {

	private static final long serialVersionUID = -4886804952243266943L;

	/**
	 * ID
	 */
	private String id;
	
	/**
	 * 企业ID
	 */
	private String corpId;

	/**
	 * 会议主题
	 */
	private String theme;

	/**
	 * 发起人用户ID
	 */
	private String userId;

	/**
	 * 发起人用户姓名
	 */
	private String userName;

	/**
	 * 会议室ID
	 */
	private String roomId;

	/**
	 * 会议室ID
	 */
	private String roomName;

	/**
	 * 预定日期
	 */
	private Date date;

	/**
	 * 会议开始时间
	 */
	private Date start;

	/**
	 * 会议结束时间
	 */
	private Date end;

	/**
	 * 是否重复预定
	 */
	private String repeat;

	/**
	 * 会议反馈消息开启状态
	 */
	private String status;

	public Meeting() {
		super();
	}

	public Meeting(String id, String corpId, String theme, String userId,
			String userName, String roomId, String roomName, Date date,
			Date start, Date end, String repeat, String status) {
		super();
		this.id = id;
		this.corpId = corpId;
		this.theme = theme;
		this.userId = userId;
		this.userName = userName;
		this.roomId = roomId;
		this.roomName = roomName;
		this.date = date;
		this.start = start;
		this.end = end;
		this.repeat = repeat;
		this.status = status;
	}

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

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Meeting [id=" + id + ", corpId=" + corpId + ", theme=" + theme + ", userId="
				+ userId + ", userName=" + userName + ", roomId=" + roomId
				+ ", roomName=" + roomName + ", date=" + date + ", start="
				+ start + ", end=" + end + ", repeat=" + repeat + ", status="
				+ status + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.energy.weixin.db.PersistentObject#getPersistentState()
	 */
	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", getId());
		persistentState.put("corpId", getCorpId());
		persistentState.put("theme", getTheme());
		persistentState.put("userId", getUserId());
		persistentState.put("userName", getUserName());
		persistentState.put("roomId", getRoomId());
		persistentState.put("roomName", getRoomName());
		persistentState.put("date", getDate());
		persistentState.put("start", getStart());
		persistentState.put("end", getEnd());
		persistentState.put("repeat", getRepeat());
		persistentState.put("status", getStatus());
		return persistentState;
	}

}
