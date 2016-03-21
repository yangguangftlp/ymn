/**
 * 
 */
package com.vyiyun.weixin.model;

import java.io.Serializable;
import java.util.List;

/**
 * 扩展信息
 * 
 * @author tf
 * 
 * @date 2015年7月30日 上午10:29:23
 * @version 1.0
 */
public class Extattr implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Attrs> attrs;

	public List<Attrs> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<Attrs> attrs) {
		this.attrs = attrs;
	}

	@Override
	public String toString() {
		return "Extattr [attrs=" + attrs + ']';
	}

}
