/**
 * 
 */
package com.vyiyun.weixin.model;

import java.io.Serializable;

/**
 * @author tf
 * 
 * @date 2015年7月30日 上午10:28:53
 * @version 1.0
 */
public class Attrs implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String value;

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

	@Override
	public String toString() {
		return "Attrs [name=" + name + ", value=" + value + ']';
	}

}
