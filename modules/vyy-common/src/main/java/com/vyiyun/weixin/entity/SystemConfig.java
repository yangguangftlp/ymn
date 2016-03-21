/**
 * 系统配置实体
 */
package com.vyiyun.weixin.entity;

import java.io.Serializable;

/**
 * @author tf
 * 
 * @date 2015年7月7日 上午9:43:21
 * @version 1.0
 */
public class SystemConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37410594754196586L;

	/**
	 * 唯一ID
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
	 * 名称
	 */
	private String name;

	/**
	 * 配置值
	 */
	private String value;
	/**
	 * 描述
	 */
	private String desc;
	/**
	 * 主题
	 */
	private String content;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public Integer getPx() {
		return px;
	}

	public void setPx(Integer px) {
		this.px = px;
	}

}
