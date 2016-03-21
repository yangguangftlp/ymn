package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * @ClassName: Expense
 * @Description: 报销类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class Expense implements Serializable, PersistentObject {

	private static final long serialVersionUID = 2973671643957111823L;

	/*
	 * ID
	 */
	private String id;
	/*
	 * /* 企业ID
	 */
	private String corpId;
	/*
	 * 发起人用户ID
	 */
	private String userId;
	/*
	 * 发起人用户姓名
	 */
	private String userName;
	/*
	 * 报销主题
	 */
	private String theme;
	/*
	 * 报销部门
	 */
	private String department;
	/*
	 * 报销事由
	 */
	private String reason;
	/*
	 * 报销总额
	 */
	private Float amount;
	/*
	 * 附件数量
	 */
	private Integer annexCount;
	/*
	 * 发起时间
	 */
	private Date createTime;

	/*
	 * 结束时间
	 */
	private Date endTime;
	/*
	 * 当前状态，1-待审核，2-审核中，3-已审核，4-待报销，5-已报销
	 */
	private String status;

	/**
	 * 报销单号
	 */
	private String expenseNum;

	/**
	 * 实际费用
	 */
	private Float actualCost;

	private String expenseType;

	public Expense() {
		super();
	}

	public Expense(String id, String corpId, String userId, String userName, String theme, String department,
			String reason, float amount, int annexCount, Date createTime, String status) {
		super();
		this.id = id;
		this.corpId = corpId;
		this.userId = userId;
		this.userName = userName;
		this.theme = theme;
		this.department = department;
		this.reason = reason;
		this.amount = amount;
		this.annexCount = annexCount;
		this.createTime = createTime;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	/**
	 * @return the corpId
	 */
	public String getCorpId() {
		return corpId;
	}

	/**
	 * @param corpId
	 *            the corpId to set
	 */
	public void setCorpId(String corpId) {
		this.corpId = corpId;
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

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Integer getAnnexCount() {
		return annexCount;
	}

	public void setAnnexCount(int annexCount) {
		this.annexCount = annexCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getExpenseNum() {
		return expenseNum;
	}

	public void setExpenseNum(String expenseNum) {
		this.expenseNum = expenseNum;
	}

	public Float getActualCost() {
		return actualCost;
	}

	public void setActualCost(Float actualCost) {
		this.actualCost = actualCost;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public void setAnnexCount(Integer annexCount) {
		this.annexCount = annexCount;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		persistentState.put("theme", theme);
		persistentState.put("department", department);
		persistentState.put("reason", reason);
		persistentState.put("department", department);
		persistentState.put("amount", amount);
		persistentState.put("annexCount", annexCount);
		persistentState.put("createTime", createTime);
		persistentState.put("endTime", endTime);
		persistentState.put("status", status);
		persistentState.put("expenseNum", expenseNum);
		persistentState.put("actualCost", actualCost);
		return persistentState;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

}
