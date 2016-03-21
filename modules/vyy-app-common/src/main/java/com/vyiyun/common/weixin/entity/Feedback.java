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
 * 问题反馈
 * 
 * @author tf
 * @date 2015年10月8日 上午9:47:44
 */
public class Feedback implements Serializable, PersistentObject {

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
	 * 用户id
	 */
	private String userId;
	/**
	 * 用户名称
	 */
	private String userName;
	/**
	 * 状态 1 待处理 2 已处理 3 已解决
	 */
	private String status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 结束时间
	 */
	private Date endTime;
	/**
	 * 问题
	 */
	private String problem;
	/**
	 * 解决建议
	 */
	private String suggest;

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

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("status", status);
		persistentState.put("userId", userId);
		persistentState.put("userName", userName);
		persistentState.put("createTime", createTime);
		persistentState.put("endTime", endTime);
		persistentState.put("problem", problem);
		persistentState.put("suggest", suggest);
		return persistentState;
	}

}
