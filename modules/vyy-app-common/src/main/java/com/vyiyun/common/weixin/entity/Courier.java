package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * 快递
 * 
 * @author tf
 * 
 * @date 下午2:55:00
 */
public class Courier implements Serializable, PersistentObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5751246444801854415L;
	/**
	 * 唯一编号
	 */
	private String id;
	/*
	 * corpId
	 */
	private String corpId;
	/**
	 * 创建者id
	 */
	private String creatorId;
	/**
	 * 创建者名称
	 */
	private String creatorName;

	/**
	 * 收件人Id
	 */
	private String consigneeId;

	/**
	 * 收件人名称
	 */
	private String consigneeName;

	/**
	 * 快递单号
	 */
	private String courierNum;

	/**
	 * 所属快递
	 */
	private String belong;

	/**
	 * 付款金额
	 */
	private Float money;

	/**
	 * 状态
	 */
	private String status;
	/**
	 * 时间
	 */
	private Date createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getConsigneeId() {
		return consigneeId;
	}

	public void setConsigneeId(String consigneeId) {
		this.consigneeId = consigneeId;
	}

	public String getConsigneeName() {
		return consigneeName;
	}

	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}

	public String getCourierNum() {
		return courierNum;
	}

	public void setCourierNum(String courierNum) {
		this.courierNum = courierNum;
	}

	public String getBelong() {
		return belong;
	}

	public void setBelong(String belong) {
		this.belong = belong;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("creatorId", creatorId);
		persistentState.put("creatorName", creatorName);
		persistentState.put("consigneeId", consigneeId);
		persistentState.put("consigneeName", consigneeName);
		persistentState.put("courierNum", courierNum);
		persistentState.put("belong", belong);
		persistentState.put("money", money);
		persistentState.put("status", status);
		persistentState.put("createTime", createTime);
		return persistentState;
	}
}
