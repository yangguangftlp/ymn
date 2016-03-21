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
 * 工作日实体
 * @author zb.shen
 * @date 2015年12月31日
 * @version 1.0
 */
public class Workday implements Serializable, PersistentObject {

	private static final long serialVersionUID = -905316142735888112L;

	// 唯一ID
	private String id;
	// 公司ID
	private String corpId;
	// 开始日
	private Date startDay;
	// 结束日
	private Date endDay;
	// 是（1）否（0）需要上班
	private String attendOrNot;
	// 年份
	private int year;
	// 备注
	private String remark;
	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("startDay", startDay);
		persistentState.put("endDay", endDay);
		persistentState.put("attendOrNot", attendOrNot);
		persistentState.put("year", year);
		persistentState.put("remark", remark);
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
	 * @return the startDay
	 */
	public Date getStartDay() {
		return startDay;
	}

	/**
	 * @param startDay the startDay to set
	 */
	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}

	/**
	 * @return the endDay
	 */
	public Date getEndDay() {
		return endDay;
	}

	/**
	 * @param endDay the endDay to set
	 */
	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}

	/**
	 * @return the attendOrNot
	 */
	public String getAttendOrNot() {
		return attendOrNot;
	}

	/**
	 * @param attendOrNot the attendOrNot to set
	 */
	public void setAttendOrNot(String attendOrNot) {
		this.attendOrNot = attendOrNot;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
