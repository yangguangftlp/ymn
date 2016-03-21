package com.vyiyun.weixin.entity;

import java.io.Serializable;

/**
 * @ClassName: Entity_File
 * @Description: 实体-文件对应类
 * @author CCLIU
 * @date 2015-6-11 上午11:05:22 v1.0
 */
public class EntityAccessory implements Serializable {

	private static final long serialVersionUID = -2347687637404299888L;

	/*
	 * 实体ID
	 */
	private String entityId;
	/*
	 * 文件ID
	 */
	private String fileId;

	/*
	 * 业务类型 默认值0 兼容之前
	 */
	private String bType = "0";

	/*
	 * 文件类型
	 */
	private String fType;

	public EntityAccessory() {
		super();
	}

	public EntityAccessory(String entityId, String fileId) {
		super();
		this.entityId = entityId;
		this.fileId = fileId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getbType() {
		return bType;
	}

	public void setbType(String bType) {
		this.bType = bType;
	}

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
	}
}
