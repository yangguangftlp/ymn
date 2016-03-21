/**
 * 
 */
package com.vyiyun.common.weixin.entity;

/**
 * 评分实体
 * 
 * @author tf
 * @date 2015年11月16日 上午10:30:34
 */
public class Score {

	/**
	 * 唯一ID
	 */
	private String id;
	/**
	 * 企业ID
	 */
	private String corpId;
	/**
	 * 问题ID
	 */
	private String problemId;
	/**
	 * 评价ID
	 */
	private String appraisalId;
	/**
	 * 分数
	 */
	private Integer score;

	/**
	 * 意见
	 */
	private String opinion;

	/**
	 * 评价人ID
	 */
	private String raterId;
	/**
	 * 评价人名称
	 */
	private String raterName;

	/**
	 * 被评价人ID
	 */
	private String userId;

	/**
	 * 被评价人名称
	 */
	private String userName;

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

	public String getProblemId() {
		return problemId;
	}

	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	public String getAppraisalId() {
		return appraisalId;
	}

	public void setAppraisalId(String appraisalId) {
		this.appraisalId = appraisalId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getRaterId() {
		return raterId;
	}

	public void setRaterId(String raterId) {
		this.raterId = raterId;
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

	public String getRaterName() {
		return raterName;
	}

	public void setRaterName(String raterName) {
		this.raterName = raterName;
	}

}
