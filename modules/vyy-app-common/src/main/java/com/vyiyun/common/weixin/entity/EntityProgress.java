package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Entity_Progress
 * @Description: 实体-进展对应类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class EntityProgress implements Serializable {

	private static final long serialVersionUID = 357303186062685469L;

	// 唯一id
	private String id;
	// 企业id
	private String corpId;
	/*
	 * 实体ID
	 */
	private String entityId;
	/*
	 * 最新进展内容
	 */
	private String content;

	/*
	 * 是否作废
	 */
	private boolean invalid;

	/**
	 * 创建日期
	 */
	private Date createTime;

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

	public EntityProgress() {
		super();
	}

	public EntityProgress(String entityId, String content) {
		super();
		this.entityId = entityId;
		this.content = content;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
