package com.vyiyun.weixin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <Description> tb_weixin_dept表PO<br>
 * 
 * @author Amos<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Dec 17, 2015 <br>
 */
public class WeixinDept implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3130398959320095324L;

	/**
	 * 部门id
	 */
	private String id;

	/**
	 * 企业corpId
	 */
	private String corpId;

	/**
	 * 部门名称
	 */
	private String name;

	/**
	 * 父部门id,根部门为1
	 */
	private String parentId;

	/**
	 * 在父部门中的次序值。order值小的排序靠
	 */
	private Integer order;

	/**
	 * 套件编号
	 */
	private String suiteIds;

	/**
	 * 子部门
	 */
	private List<WeixinDept> child;

	/**
	 * get id
	 * 
	 * @return Returns the id.<br>
	 */
	public String getId() {
		return id;
	}

	/**
	 * set id
	 * 
	 * @param id
	 *            The id to set. <br>
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * get corpId
	 * 
	 * @return Returns the corpId.<br>
	 */
	public String getCorpId() {
		return corpId;
	}

	/**
	 * set corpId
	 * 
	 * @param corpId
	 *            The corpId to set. <br>
	 */
	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	/**
	 * get name
	 * 
	 * @return Returns the name.<br>
	 */
	public String getName() {
		return name;
	}

	/**
	 * set name
	 * 
	 * @param name
	 *            The name to set. <br>
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * get parentId
	 * 
	 * @return Returns the parentId.<br>
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * set parentId
	 * 
	 * @param parentId
	 *            The parentId to set. <br>
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * get order
	 * 
	 * @return Returns the order.<br>
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * set order
	 * 
	 * @param order
	 *            The order to set. <br>
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * get child
	 * 
	 * @return Returns the child.<br>
	 */
	public List<WeixinDept> getChild() {
		return child;
	}

	/**
	 * Description: <br>
	 * 
	 * @author Amos<br>
	 * @taskId <br>
	 * @param dept
	 * <br>
	 */
	public void addChild(WeixinDept dept) {
		if (null != dept) {
			if (null == this.child) {
				this.child = new ArrayList<WeixinDept>();
			}
			this.child.add(dept);
		}
	}

	/**
	 * get suiteIds
	 * 
	 * @return Returns the suiteIds.<br>
	 */
	public String getSuiteIds() {
		return suiteIds;
	}

	/**
	 * set suiteIds
	 * 
	 * @param suiteIds
	 *            The suiteIds to set. <br>
	 */
	public void setSuiteIds(String suiteIds) {
		this.suiteIds = suiteIds;
	}

}
