/**
 * 
 */
package com.vyiyun.common.weixin.entity;

import java.util.Date;

/**
 * 应用访问实体
 * 
 * @author tf
 * @date 2015年10月8日 下午3:46:58
 */
public class AppAccess {

	/**
	 * 唯一ID
	 */
	private String id;
	/**
	 * 应用ID
	 */
	private String appId;
	/**
	 * 应用名称
	 */
	private String appName;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 访问时间
	 */
	private Date accessDate;
	
	public AppAccess(){}
	
	public AppAccess(String id, String appId, String appName, String corpId, String userId,
			String userName, Date accessDate) {
		super();
		this.id = id;
		this.appId = appId;
		this.appName = appName;
		this.appName = appName;
		this.corpId = corpId;
		this.userName = userName;
		this.accessDate = accessDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCorpId() {
		return corpId;
	}

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

	public Date getAccessDate() {
		return accessDate;
	}

	public void setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
	}
}
