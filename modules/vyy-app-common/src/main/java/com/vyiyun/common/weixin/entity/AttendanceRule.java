package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * @ClassName: AttendanceRule
 * @Description: 签到规则类
 * @author zb.shen
 * @date 2015年12月29日
 */
public class AttendanceRule implements Serializable, PersistentObject {

	private static final long serialVersionUID = 3426359920089143003L;

	/*
	 * ID
	 */
	private String id;
	/*
	 * CorpId
	 */
	private String corpId;
	/*
	 * 考勤开始时
	 */
	private String startHour;
	/*
	 * 考勤开始分
	 */
	private String startMinute;
	/*
	 * 考勤结束时
	 */
	private String endHour;
	/*
	 * 考勤结束分
	 */
	private String endMinute;
	/*
	 * 签到缓冲分钟数
	 */
	private String delayMinutes;
	/*
	 * 周日
	 */
	private String sun;
	/*
	 * 周一
	 */
	private String mon;
	/*
	 * 周二
	 */
	private String tues;
	/*
	 * 周三
	 */
	private String wed;
	/*
	 * 周四
	 */
	private String thur;
	/*
	 * 周五
	 */
	private String fri;
	/*
	 * 周六
	 */
	private String sat;
	/*
	 * 创建时间
	 */
	private Date createTime;
	/*
	 * 更新时间
	 */
	private Date updateTime;

	public AttendanceRule() {
		super();
	}

	/**
	 * @param id 考勤规则ID
	 * @param corpId 企业ID
	 */
	public AttendanceRule(String id, String corpId) {
		super();
		this.id = id;
		this.corpId = corpId;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("startHour", startHour);
		persistentState.put("startMinute", startMinute);
		persistentState.put("endHour", endHour);
		persistentState.put("endMinute", endMinute);
		persistentState.put("delayMinutes", delayMinutes);
		persistentState.put("sun", sun);
		persistentState.put("mon", mon);
		persistentState.put("tues", tues);
		persistentState.put("wed", wed);
		persistentState.put("thur", thur);
		persistentState.put("fri", fri);
		persistentState.put("sat", sat);
		persistentState.put("createTime", createTime);
		persistentState.put("updateTime", updateTime);

		return persistentState;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the corpId
	 */
	public String getCorpId() {
		return corpId;
	}

	/**
	 * @param corpId the corpId to set
	 */
	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	/**
	 * @return the startHour
	 */
	public String getStartHour() {
		return startHour;
	}

	/**
	 * @param startHour the startHour to set
	 */
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	/**
	 * @return the startMinute
	 */
	public String getStartMinute() {
		return startMinute;
	}

	/**
	 * @param startMinute the startMinute to set
	 */
	public void setStartMinute(String startMinute) {
		this.startMinute = startMinute;
	}

	/**
	 * @return the endHour
	 */
	public String getEndHour() {
		return endHour;
	}

	/**
	 * @param endHour the endHour to set
	 */
	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	/**
	 * @return the endMinute
	 */
	public String getEndMinute() {
		return endMinute;
	}

	/**
	 * @param endMinute the endMinute to set
	 */
	public void setEndMinute(String endMinute) {
		this.endMinute = endMinute;
	}

	/**
	 * @return the delayMinutes
	 */
	public String getDelayMinutes() {
		return delayMinutes;
	}

	/**
	 * @param delayMinutes the delayMinutes to set
	 */
	public void setDelayMinutes(String delayMinutes) {
		this.delayMinutes = delayMinutes;
	}

	/**
	 * @return the sun
	 */
	public String getSun() {
		return sun;
	}

	/**
	 * @param sun the sun to set
	 */
	public void setSun(String sun) {
		this.sun = sun;
	}

	/**
	 * @return the mon
	 */
	public String getMon() {
		return mon;
	}

	/**
	 * @param mon the mon to set
	 */
	public void setMon(String mon) {
		this.mon = mon;
	}

	/**
	 * @return the tues
	 */
	public String getTues() {
		return tues;
	}

	/**
	 * @param tues the tues to set
	 */
	public void setTues(String tues) {
		this.tues = tues;
	}

	/**
	 * @return the wed
	 */
	public String getWed() {
		return wed;
	}

	/**
	 * @param wed the wed to set
	 */
	public void setWed(String wed) {
		this.wed = wed;
	}

	/**
	 * @return the thur
	 */
	public String getThur() {
		return thur;
	}

	/**
	 * @param thur the thur to set
	 */
	public void setThur(String thur) {
		this.thur = thur;
	}

	/**
	 * @return the fri
	 */
	public String getFri() {
		return fri;
	}

	/**
	 * @param fri the fri to set
	 */
	public void setFri(String fri) {
		this.fri = fri;
	}

	/**
	 * @return the sat
	 */
	public String getSat() {
		return sat;
	}

	/**
	 * @param sat the sat to set
	 */
	public void setSat(String sat) {
		this.sat = sat;
	}

	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
