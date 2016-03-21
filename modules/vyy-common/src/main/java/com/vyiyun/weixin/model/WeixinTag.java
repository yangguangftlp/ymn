/**
 * 
 */
package com.vyiyun.weixin.model;

import java.io.Serializable;

/**
 * 微信标签
 * 
 * @author tf
 * 
 * @date 上午11:43:09
 */
@SuppressWarnings("serial")
public class WeixinTag implements Serializable{
	/**
	 * 标签ID
	 */
	private String tagid;
	/**
	 * 企业corpId
	 */
	private String corpId;
	/**
	 * 标签名称
	 */
	private String tagname;

	public String getTagid() {
		return tagid;
	}

	public void setTagid(String tagid) {
		this.tagid = tagid;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
}
