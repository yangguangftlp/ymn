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
 * 借款实体
 * 
 * @author tf
 * @date 2015年11月3日 下午1:52:12
 */
public class Loan implements Serializable, PersistentObject {

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
	 * 部门
	 */
	private String department;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 申请日期
	 */
	private Date applyDate;
	/**
	 * 申请金额
	 */
	private Float amount;

	/**
	 * 大写金额
	 */
	private String capitalAmount;

	/**
	 * 事由
	 */
	private String subject;

	/**
	 * 使用明细
	 */
	private String details;

	/**
	 * 开户行
	 */
	private String bank;

	/**
	 * 收款账号
	 */
	private String receiveAccount;

	/**
	 * 合同金额
	 */
	private Float contractAmount;

	/**
	 * 剩余金额
	 */
	private Float remainingAmount;

	/**
	 * 客户名称
	 */
	private String clientName;

	/**
	 * 项目名称
	 */
	private String projectName;

	/**
	 * 借款说明
	 */
	private String remark;

	/**
	 * 借款单号
	 */
	private String loanNum;

	/**
	 * 借款用途
	 */
	private String loanUse;

	/**
	 * 借款类型 0 私有 1 公有
	 */
	private String loanType;

	/**
	 * 公司
	 */
	private String company;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 状态
	 */
	private String status;

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

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getCapitalAmount() {
		return capitalAmount;
	}

	public void setCapitalAmount(String capitalAmount) {
		this.capitalAmount = capitalAmount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getReceiveAccount() {
		return receiveAccount;
	}

	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}

	public Float getContractAmount() {
		return contractAmount;
	}

	public void setContractAmount(Float contractAmount) {
		this.contractAmount = contractAmount;
	}

	public Float getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(Float remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLoanNum() {
		return loanNum;
	}

	public void setLoanNum(String loanNum) {
		this.loanNum = loanNum;
	}

	public String getLoanUse() {
		return loanUse;
	}

	public void setLoanUse(String loanUse) {
		this.loanUse = loanUse;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("department", department);
		persistentState.put("applyDate", applyDate);
		persistentState.put("amount", amount);
		persistentState.put("capitalAmount", capitalAmount);
		persistentState.put("subject", subject);
		persistentState.put("details", details);
		persistentState.put("bank", bank);
		persistentState.put("receiveAccount", receiveAccount);
		persistentState.put("contractAmount", contractAmount);
		persistentState.put("remainingAmount", remainingAmount);
		persistentState.put("clientName", clientName);
		persistentState.put("projectName", projectName);
		persistentState.put("remark", remark);
		persistentState.put("loanNum", loanNum);
		persistentState.put("loanUse", loanUse);
		persistentState.put("loanType", loanType);
		persistentState.put("company", company);
		persistentState.put("createTime", createTime);
		persistentState.put("endTime", endTime);
		persistentState.put("status", status);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		return persistentState;
	}
}
