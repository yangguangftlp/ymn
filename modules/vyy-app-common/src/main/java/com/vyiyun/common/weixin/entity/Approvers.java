/**
 * 
 */
package com.vyiyun.common.weixin.entity;

/**
 * 审批人
 * 
 * @author tf
 * @date 2015年11月10日 下午2:06:58
 */
public class Approvers {

	/**
	 * 唯一id
	 */
	private String id;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 父ID
	 */
	private String parentId;
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
	 * 图像url
	 */
	private String avatar;

	/**
	 * 下一级审核人
	 */
	private String next;

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

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

}
