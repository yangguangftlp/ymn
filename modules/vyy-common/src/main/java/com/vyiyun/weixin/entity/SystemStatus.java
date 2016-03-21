/**
 * 
 */
package com.vyiyun.weixin.entity;

import java.io.Serializable;

/**
 * 
 * @author tf
 * 
 *         2015年6月27日 下午6:32:58
 */
public class SystemStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 唯一id
	 */
	private String id;
	/**
	 * 企业号ID
	 */
	private String corpId;
	/**
	 * 类型
	 */
	private String type;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 名称
	 */
	private String name;
	/**
	 * 状态值
	 */
	private String value;
	/**
	 * 排序
	 */
	private Integer px;

	public SystemStatus() {
		super();
	}

	public SystemStatus(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
