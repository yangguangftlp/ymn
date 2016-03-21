package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * @ClassName: Approval
 * @Description: 审批类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class Approval implements Serializable, PersistentObject {

	private static final long serialVersionUID = 7190048898559281652L;

	/*
	 * ID
	 */
	private String id;
	/*
	 * 企业ID
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
	 * 流程名称
	 */
	private String flowName;
	/*
	 * 归属部门
	 */
	private String department;
	/*
	 * 审批内容
	 */
	private String content;
	/*
	 * 备注
	 */
	private String remark;
	/*
	 * 合同编号
	 */
	private String contractNumber;
	/*
	 * 合作方
	 */
	private String partner;
	/*
	 * 发起时间
	 */
	private Date createTime;
	/*
	 * 流程类型，PT为普通类型，HT为合同类型
	 */
	private String flowType;

	/*
	 * 结束时间
	 */
	private Date endTime;

	/*
	 * 当前状态，1-待审核，2-审核中，3-已审核
	 */
	private String status;

	/**
	 * 审批新增状态为 需求:申请抄送给财务，财务需要显示"已办"
	 */
	private String cstatus;

	public Approval() {
	}

	public Approval(String id, String corpId, String userId, String userName, String flowName, String department, String content,
			String remark, String contractNumber, String partner, Date createTime, String flowType, String status) {
		super();
		this.id = id;
		this.corpId = corpId;
		this.userId = userId;
		this.userName = userName;
		this.flowName = flowName;
		this.department = department;
		this.content = content;
		this.remark = remark;
		this.contractNumber = contractNumber;
		this.partner = partner;
		this.createTime = createTime;
		this.flowType = flowType;
		this.status = status;
	}

	public String getId() {
		return id;
	}

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

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getPartner() {
		return partner;
	}

	public void setPartner(String partner) {
		this.partner = partner;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCstatus() {
		return cstatus;
	}

	public void setCstatus(String cstatus) {
		this.cstatus = cstatus;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		persistentState.put("flowName", flowName);
		persistentState.put("department", department);
		persistentState.put("content", content);
		persistentState.put("department", department);
		persistentState.put("remark", remark);
		persistentState.put("contractNumber", contractNumber);
		persistentState.put("partner", partner);
		persistentState.put("createTime", createTime);
		persistentState.put("flowType", flowType);
		persistentState.put("endTime", endTime);
		persistentState.put("status", status);
		persistentState.put("cstatus", cstatus);
		return persistentState;
	}
}
