/**
 * 
 */
package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * 问题模板
 * 
 * @author tf
 * @date 2015年11月16日 上午10:27:24
 */
public class ProblemTemplate implements Serializable, PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1933846138881949595L;
	/**
	 * 唯一ID
	 */
	private String id;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 指标
	 */
	private String quota;
	/**
	 * 标准
	 */
	private String standard;
	/**
	 * 分值
	 */
	private Integer scores;

	/**
	 * 评价ID
	 */
	private String appraisalId;

	/**
	 * 排序
	 */
	private Integer px;

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

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public Integer getScores() {
		return scores;
	}

	public void setScores(Integer scores) {
		this.scores = scores;
	}

	public String getAppraisalId() {
		return appraisalId;
	}

	public void setAppraisalId(String appraisalId) {
		this.appraisalId = appraisalId;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("appraisalId", appraisalId);
		persistentState.put("quota", quota);
		persistentState.put("standard", standard);
		persistentState.put("scores", scores);
		persistentState.put("px", px);
		return persistentState;
	}

}
