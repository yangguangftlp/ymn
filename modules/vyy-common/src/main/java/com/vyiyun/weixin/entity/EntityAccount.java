package com.vyiyun.weixin.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Entity_Account
 * @Description: 实体-账户对应类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class EntityAccount implements Serializable {

	private static final long serialVersionUID = -8429861658346218205L;

	/**
	 * 唯一id
	 */
	private String id;
	
	/**
	 * 企业ID
	 */
	private String corpId;

	/*
	 * 实体ID
	 */
	private String entityId;
	/*
	 * 实体类型，QD为签到，QJ为请假，BX为报销，SP为审批，HY为会议室预定，XM为项目管理
	 */
	private String entityType;
	/*
	 * 账户ID
	 */
	private String accountId;

	/*
	 * 账户名称
	 */
	private String accountName;

	/*
	 * 账户类型，D为部门，T为标签，U为用户
	 */
	private String accountType;
	/*
	 * 类型，SH为审核，CS为抄送，CW为财务
	 */
	private String personType;
	/*
	 * 处理结果，0为未处理1同意 2拒绝3打回
	 */
	private String dealResult;
	/*
	 * 目标日期，用于签到
	 */
	private Date targetDate;
	/*
	 * 备注/拒绝原因
	 */
	private String remark;
	/*
	 * 图像url
	 */
	private String avatar;
	/*
	 * 更新时间
	 */
	private Date updateTime;

	/*
	 * 是否作废，1作废，0未作废
	 */
	private Boolean invalid;

	/**
	 * 下一级审核人
	 */
	private String next;

	/*
	 * 评价次数默认0 只针对评价
	 */
	private Integer appraiseTimes;
	/*
	 * 评价人数默认0 只针对评价
	 */
	private Integer appraiseUsers;

	public EntityAccount() {
	}

	public EntityAccount(String entityId, String entityType, String accountId, String accountType, String personType,
			String dealResult, Date targetDate, String remark) {
		super();
		this.entityId = entityId;
		this.entityType = entityType;
		this.accountId = accountId;
		this.accountType = accountType;
		this.personType = personType;
		this.dealResult = dealResult;
		this.targetDate = targetDate;
		this.remark = remark;
	}

	/**
	 * 包含avatar的构造函数
	 * 
	 * @param entityId
	 * @param entityType
	 * @param accountId
	 * @param accountType
	 * @param personType
	 * @param dealResult
	 * @param targetDate
	 * @param remark
	 * @param avatar
	 */
	public EntityAccount(String entityId, String entityType, String accountId, String accountType, String personType,
			String dealResult, Date targetDate, String remark, String avatar) {
		super();
		this.entityId = entityId;
		this.entityType = entityType;
		this.accountId = accountId;
		this.accountType = accountType;
		this.personType = personType;
		this.dealResult = dealResult;
		this.targetDate = targetDate;
		this.remark = remark;
		this.avatar = avatar;
	}

	/**
	 * 用于查询时的构造函数
	 * 
	 * @param entityId
	 */
	public EntityAccount(String entityId, String corpId) {
		super();
		this.entityId = entityId;
		this.corpId = corpId;
	}

	/**
	 * 用于修改记录时的构造函数
	 * 
	 * @param entityId
	 */
	public EntityAccount(String entityId, String accountId, String dealResult) {
		super();
		this.entityId = entityId;
		this.accountId = accountId;
		this.dealResult = dealResult;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getDealResult() {
		return dealResult;
	}

	public void setDealResult(String dealResult) {
		this.dealResult = dealResult;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Boolean getInvalid() {
		return invalid;
	}

	public void setInvalid(Boolean invalid) {
		this.invalid = invalid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public Integer getAppraiseTimes() {
		return appraiseTimes;
	}

	public void setAppraiseTimes(Integer appraiseTimes) {
		this.appraiseTimes = appraiseTimes;
	}

	public Integer getAppraiseUsers() {
		return appraiseUsers;
	}

	public void setAppraiseUsers(Integer appraiseUsers) {
		this.appraiseUsers = appraiseUsers;
	}
}
