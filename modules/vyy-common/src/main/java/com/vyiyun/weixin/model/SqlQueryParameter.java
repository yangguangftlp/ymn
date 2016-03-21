/**
 * 
 */
package com.vyiyun.weixin.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页实体
 * 
 * @author tf
 * 
 */
public class SqlQueryParameter {
	// 页码，默认是第一页
	private int pageIndex = 1;
	// 每页显示的记录数，默认是15
	private int pageSize = 15;
	// 记录总数 该字段只有在分页是生效
	private int totalRecord;
	// 是否分页默认不分页
	private boolean isPage;
	// 分页参数对应实体
	private Object parameter;
	// 其他参数
	private Map<String, Object> keyValMap = new HashMap<String, Object>();

	private String orderBy;

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean isPage) {
		this.isPage = isPage;
	}

	public Map<String, Object> getKeyValMap() {
		return keyValMap;
	}

	public void setKeyValMap(Map<String, Object> keyValMap) {
		if (null != parameter) {
			this.keyValMap = keyValMap;
		}
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

}
