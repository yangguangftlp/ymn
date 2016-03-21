package com.vyiyun.weixin.model;

import java.util.ArrayList;

/**
 * 数据模型
 * 
 * @author tf
 * 
 *         2015年6月25日
 * @param <T>
 */
public class DataResult {

	/**
	 * 总数
	 */
	private int total;
	/**
	 * 数据
	 */
	private Object data = new ArrayList<String>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
