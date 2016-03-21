/**
 * 
 */
package com.vyiyun.common.weixin.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.vyiyun.weixin.db.PersistentObject;

/**
 * 报销费用
 * 
 * @author tf
 * @date 2015年10月23日 下午3:19:12
 */
public class ExpenseFee implements Serializable, PersistentObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String corpId;
	private String expenseId;
	private String category;
	private Float money;

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

	public String getExpenseId() {
		return expenseId;
	}

	public void setExpenseId(String expenseId) {
		this.expenseId = expenseId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	@Override
	public Map<String, Object> getPersistentState() {
		Map<String, Object> persistentState = new HashMap<String, Object>();
		persistentState.put("id", id);
		persistentState.put("corpId", corpId);
		persistentState.put("expenseId", expenseId);
		persistentState.put("category", category);
		persistentState.put("money", money);
		return persistentState;
	}
}
